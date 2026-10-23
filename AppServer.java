import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.util.DBConn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppServer {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String DB_USER = "scott";
    private static final String DB_PASSWORD = "tiger";

    public static void main(String[] args) throws IOException {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/hello", AppServer::handleHello);
        server.createContext("/echo", AppServer::handleEcho);
        server.createContext("/users", AppServer::handleUsers);
        server.createContext("/main", AppServer::handleMain); // 1~7번 메뉴 모음
        server.createContext("/sellers", AppServer::handleSellerList); // 1. 판매자 목록보기
        server.createContext("/user-list", AppServer::handleUserList); // 2. 유저목록보기
        server.createContext("/seller-products", AppServer::handleSellerProducts); // 3. 판매자가 등록한 상품 전체조회(판매자ID)
        server.createContext("/product-options", AppServer::handleProductOptions); // 4. 특정상품에 대한 모든 옵션조회(상품ID)
        server.createContext("/product-inquiries", AppServer::handleProductInquiries); // 5. 특정상품의 문의답변표시(상품번호)
        server.createContext("/products", AppServer::handleProductList); // 6. 모든 상품정보조회
        server.createContext("/category-products", AppServer::handleCategoryProducts); // 7. 특정카테고리의 상품정보조회
        server.createContext("/categorys", AppServer::handleCategoryList); // 모든 카테고리 조회
        server.createContext("/product_detail", AppServer::handleProductDetail); // 상품 상세정보 + 옵션 전체조회(상품번호)
        server.createContext("/product-register", AppServer::handleProductRegister); // 상품 + 옵션 등록
        server.createContext("/alluser_payment_history", AppServer::handleAllUserPaymentHistory); // 전체 유저 결제내역 조회

        server.setExecutor(null);
        server.start();

        System.out.println("서버 시작: http://localhost:" + port);
    }

    // 페이지 상단에 항상 표시되는 1~7번 메뉴 네비게이션
    private static final String NAV_BAR =
            "<nav style=\"margin-bottom:16px; padding-bottom:8px; border-bottom:2px solid #333;\">"
            + "<a href=\"/main\" style=\"margin-right:16px; font-weight:bold;\">메인</a>"
            + "<a href=\"/sellers\" style=\"margin-right:12px;\">1. 판매자 목록</a>"
            + "<a href=\"/user-list\" style=\"margin-right:12px;\">2. 유저 목록</a>"
            + "<form action=\"/seller-products\" method=\"get\" style=\"display:inline-block; margin-right:12px;\">"
            + "3. 판매자 상품 <input type=\"number\" name=\"sellerId\" size=\"4\" required><button type=\"submit\">조회</button></form>"
            + "<form action=\"/product-options\" method=\"get\" style=\"display:inline-block; margin-right:12px;\">"
            + "4. 상품 옵션 <input type=\"number\" name=\"productId\" size=\"4\" required><button type=\"submit\">조회</button></form>"
            + "<form action=\"/product-inquiries\" method=\"get\" style=\"display:inline-block; margin-right:12px;\">"
            + "5. 문의/답변 <input type=\"number\" name=\"productNo\" size=\"4\" required><button type=\"submit\">조회</button></form>"
            + "<a href=\"/products\" style=\"margin-right:12px;\">6. 전체 상품</a>"
            + "<form action=\"/category-products\" method=\"get\" style=\"display:inline-block; margin-right:12px;\">"
            + "7. 카테고리 상품 <input type=\"number\" name=\"categoryNo\" size=\"6\" required><button type=\"submit\">조회</button></form>"
            + "<a href=\"/categorys\" style=\"margin-right:12px;\">전체 카테고리</a>"
            + "<a href=\"/product-register\" style=\"margin-right:12px;\">상품 등록</a>"
            + "<a href=\"/alluser_payment_history\" style=\"margin-right:12px;\">전체 유저 결제내역</a>"
            + "</nav>";

    private static void handleHello(HttpExchange exchange) throws IOException {
        sendText(exchange, 200, "Hello, World!");
    }

    // GET /main -> 1~7번 메뉴를 한 페이지에서 볼 수 있는 진입점
    private static void handleMain(HttpExchange exchange) throws IOException {
        String body = "<p>상단 메뉴에서 조회할 항목을 선택하세요.</p>";
        sendHtml(exchange, 200, renderPage("메인", body));
    }

    // GET /echo?message=아무말 -> 받은 쿼리 파라미터를 그대로 돌려줌
    private static void handleEcho(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "GET만 지원합니다.");
            return;
        }

        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String message = params.getOrDefault("message", "(message 파라미터 없음)");
        sendText(exchange, 200, "echo: " + message);
    }

    private static void handleUsers(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            handleGetUser(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            sendText(exchange, 200, "TODO(Step 4 이후): 사용자 생성");
        } else {
            sendText(exchange, 405, method + "는 지원하지 않습니다.");
        }
    }

    // GET /users?id=1 -> member 테이블에서 조회
    private static void handleGetUser(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String idParam = params.get("id");

        if (idParam == null) {
            sendText(exchange, 400, "id 쿼리 파라미터가 필요합니다. 예: /users?id=1");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "id는 숫자여야 합니다: " + idParam);
            return;
        }

        String sql = "SELECT member_no, member_name, email FROM member WHERE member_no = ?";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String body = "id=" + rs.getLong("member_no")
                            + ", name=" + rs.getString("member_name")
                            + ", email=" + rs.getString("email");
                    sendText(exchange, 200, body);
                } else {
                    sendText(exchange, 404, "id=" + id + " 사용자를 찾을 수 없습니다.");
                }
            }
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 1. 판매자 목록보기
    private static void handleSellerList(HttpExchange exchange) throws IOException {
        String sql = "SELECT * FROM seller";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            sendHtml(exchange, 200, renderPage("판매자 목록", tableHtml(rs)));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 2. 유저목록보기
    private static void handleUserList(HttpExchange exchange) throws IOException {
        String sql = "SELECT * FROM member";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            sendHtml(exchange, 200, renderPage("유저 목록", tableHtml(rs)));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // GET /seller-products?sellerId=1 -> 해당 판매자가 등록한 상품 전체조회
    private static void handleSellerProducts(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String sellerIdParam = params.get("sellerId");

        if (sellerIdParam == null) {
            sendText(exchange, 400, "sellerId 쿼리 파라미터가 필요합니다. 예: /seller-products?sellerId=1");
            return;
        }

        long sellerId;
        try {
            sellerId = Long.parseLong(sellerIdParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "sellerId는 숫자여야 합니다: " + sellerIdParam);
            return;
        }

        String sql = "SELECT p.*, "
                + "       s.store_name, "
                + "       mc.main_category_name, "
                + "       md.mid_category_name, "
                + "       sc.sub_category_name "
                + "FROM   PRODUCT p "
                + "       JOIN SELLER        s  ON p.seller_no       = s.seller_no "
                + "       JOIN SUB_CATEGORY  sc ON p.sub_category_no = sc.sub_category_no "
                + "       JOIN MID_CATEGORY  md ON sc.mid_category_no = md.mid_category_no "
                + "       JOIN MAIN_CATEGORY mc ON md.main_category_no = mc.main_category_no "
                + "WHERE  p.seller_no = ?";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sellerId);

            try (ResultSet rs = stmt.executeQuery()) {
                sendHtml(exchange, 200, renderPage("판매자(" + sellerId + ") 등록 상품 목록", tableHtml(rs)));
            }
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // GET /product-options?productId=4 -> 특정상품에 대한 모든 옵션조회
    private static void handleProductOptions(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String productIdParam = params.get("productId");

        if (productIdParam == null) {
            sendText(exchange, 400, "productId 쿼리 파라미터가 필요합니다. 예: /product-options?productId=4");
            return;
        }

        long productId;
        try {
            productId = Long.parseLong(productIdParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "productId는 숫자여야 합니다: " + productIdParam);
            return;
        }

        String sql = "SELECT "
                + "    S.STORE_NAME, "
                + "    P.PRODUCT_NO, "
                + "    P.PRODUCT_NAME, "
                + "    O.OPTION_ID, "
                + "    O.OPTION1_TYPE, "
                + "    O.OPTION1_VALUE, "
                + "    O.OPTION2_TYPE, "
                + "    O.OPTION2_VALUE, "
                + "    O.OPTION3_TYPE, "
                + "    O.OPTION3_VALUE, "
                + "    NVL(O.PRICE, P.PRODUCT_PRICE)    AS 판매가, "
                + "    O.QUANTITY                        AS 재고, "
                + "    O.STATUS                          AS 판매여부 "
                + "FROM PRODUCT_OPTION O "
                + "JOIN PRODUCT P ON O.PRODUCT_NO  = P.PRODUCT_NO "
                + "JOIN SELLER  S ON P.SELLER_NO   = S.SELLER_NO "
                + "WHERE P.PRODUCT_NO = ? "
                + "ORDER BY O.PRODUCT_NO, O.OPTION_ID";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                sendHtml(exchange, 200, renderPage("상품(" + productId + ") 옵션 목록", tableHtml(rs)));
            }
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // GET /product-inquiries?productNo=1 -> 특정상품의 문의답변표시
    private static void handleProductInquiries(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String productNoParam = params.get("productNo");

        if (productNoParam == null) {
            sendText(exchange, 400, "productNo 쿼리 파라미터가 필요합니다. 예: /product-inquiries?productNo=1");
            return;
        }

        long productNo;
        try {
            productNo = Long.parseLong(productNoParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "productNo는 숫자여야 합니다: " + productNoParam);
            return;
        }

        String sql = "SELECT po.OPTION_ID, "
                + "       pi.inquiry_no, "
                + "       pi.inquiry_content, "
                + "       pi.inquiry_date, "
                + "       pi.member_no, "
                + "       pia.answer_no, "
                + "       pia.answer_content, "
                + "       pia.answer_date, "
                + "       pia.seller_no "
                + "FROM   PRODUCT_OPTION po "
                + "       JOIN PRODUCT_INQUIRY        pi  ON po.OPTION_ID  = pi.OPTION_ID "
                + "       LEFT JOIN PRODUCT_INQUIRY_ANSWER pia ON pi.inquiry_no = pia.inquiry_no "
                + "WHERE  po.product_no = ? "
                + "ORDER  BY po.OPTION_ID, pi.inquiry_no, pia.answer_no";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productNo);

            try (ResultSet rs = stmt.executeQuery()) {
                sendHtml(exchange, 200, renderPage("상품(" + productNo + ") 문의/답변 목록", inquiryAnswerHtml(rs)));
            }
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 6. 모든 상품정보조회
    private static void handleProductList(HttpExchange exchange) throws IOException {
        String sql = "SELECT * FROM PRODUCT";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            sendHtml(exchange, 200, renderPage("전체 상품 목록", tableHtml(rs)));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 전체 유저 결제내역 조회
    private static void handleAllUserPaymentHistory(HttpExchange exchange) throws IOException {
        String sql = "SELECT * FROM V_PAYMENT_HISTORY";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            sendHtml(exchange, 200, renderPage("전체 유저 결제내역", tableHtml(rs)));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // GET /category-products?categoryNo=100202 -> 특정카테고리(소분류)의 상품정보조회
    private static void handleCategoryProducts(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String categoryNoParam = params.get("categoryNo");

        if (categoryNoParam == null) {
            sendText(exchange, 400, "categoryNo 쿼리 파라미터가 필요합니다. 예: /category-products?categoryNo=100202");
            return;
        }

        long categoryNo;
        try {
            categoryNo = Long.parseLong(categoryNoParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "categoryNo는 숫자여야 합니다: " + categoryNoParam);
            return;
        }

        String sql = "SELECT p.product_no, "
                + "       p.product_name, "
                + "       p.product_price, "
                + "       p.quantity, "
                + "       p.seller_no, "
                + "       sc.sub_category_name "
                + "FROM   PRODUCT p "
                + "       JOIN SUB_CATEGORY sc ON p.sub_category_no = sc.sub_category_no "
                + "WHERE  p.sub_category_no = ?";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoryNo);

            try (ResultSet rs = stmt.executeQuery()) {
                sendHtml(exchange, 200, renderPage("카테고리(" + categoryNo + ") 상품 목록", categoryProductsTableHtml(rs)));
            }
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 카테고리 상품 목록을 HTML 테이블로 변환. product_no 셀은 /product_detail 로 연결되는 링크로 표시
    private static String categoryProductsTableHtml(ResultSet rs) throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<table><tr>")
                .append("<th>product_no</th><th>product_name</th><th>product_price</th>")
                .append("<th>quantity</th><th>seller_no</th><th>sub_category_name</th>")
                .append("</tr>");

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String productNo = rs.getString("product_no");

            html.append("<tr>");
            html.append("<td><a href=\"/product_detail?productNo=").append(productNo).append("\">")
                    .append(escapeHtml(productNo)).append("</a></td>");
            html.append("<td>").append(escapeHtml(rs.getString("product_name"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("product_price"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("quantity"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("seller_no"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("sub_category_name"))).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        if (rowCount == 0) {
            html.append("<p>조회된 데이터가 없습니다.</p>");
        }
        return html.toString();
    }

    // GET /product_detail?productNo=4 -> 상품 1개의 상세정보 + 해당 상품의 모든 옵션조회
    private static void handleProductDetail(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String productNoParam = params.get("productNo");

        if (productNoParam == null) {
            sendText(exchange, 400, "productNo 쿼리 파라미터가 필요합니다. 예: /product_detail?productNo=4");
            return;
        }

        long productNo;
        try {
            productNo = Long.parseLong(productNoParam);
        } catch (NumberFormatException e) {
            sendText(exchange, 400, "productNo는 숫자여야 합니다: " + productNoParam);
            return;
        }

        String productSql = "SELECT p.*, "
                + "       mc.main_category_name, "
                + "       md.mid_category_name, "
                + "       sc.sub_category_name "
                + "FROM   PRODUCT p "
                + "       JOIN SUB_CATEGORY  sc ON p.sub_category_no  = sc.sub_category_no "
                + "       JOIN MID_CATEGORY  md ON sc.mid_category_no = md.mid_category_no "
                + "       JOIN MAIN_CATEGORY mc ON md.main_category_no = mc.main_category_no "
                + "WHERE  p.product_no = ?";

        String optionSql = "SELECT "
                + "    S.STORE_NAME, "
                + "    P.PRODUCT_NO, "
                + "    P.PRODUCT_NAME, "
                + "    O.OPTION_ID, "
                + "    O.OPTION1_TYPE, "
                + "    O.OPTION1_VALUE, "
                + "    O.OPTION2_TYPE, "
                + "    O.OPTION2_VALUE, "
                + "    O.OPTION3_TYPE, "
                + "    O.OPTION3_VALUE, "
                + "    NVL(O.PRICE, P.PRODUCT_PRICE)    AS 판매가, "
                + "    O.QUANTITY                        AS 재고, "
                + "    O.STATUS                          AS 판매여부 "
                + "FROM PRODUCT_OPTION O "
                + "JOIN PRODUCT P ON O.PRODUCT_NO  = P.PRODUCT_NO "
                + "JOIN SELLER  S ON P.SELLER_NO   = S.SELLER_NO "
                + "WHERE P.PRODUCT_NO = ? "
                + "ORDER BY O.PRODUCT_NO, O.OPTION_ID";

        String inquirySql = "SELECT po.OPTION_ID, "
                + "       pi.inquiry_no, "
                + "       pi.inquiry_content, "
                + "       pi.inquiry_date, "
                + "       pi.member_no, "
                + "       pia.answer_no, "
                + "       pia.answer_content, "
                + "       pia.answer_date, "
                + "       pia.seller_no "
                + "FROM   PRODUCT_OPTION po "
                + "       JOIN PRODUCT_INQUIRY        pi  ON po.OPTION_ID  = pi.OPTION_ID "
                + "       LEFT JOIN PRODUCT_INQUIRY_ANSWER pia ON pi.inquiry_no = pia.inquiry_no "
                + "WHERE  po.product_no = ? "
                + "ORDER  BY po.OPTION_ID, pi.inquiry_no, pia.answer_no";

        String reviewSql = "SELECT r.review_no, "
                + "       p.product_name, "
                + "       m.member_name, "
                + "       r.rating, "
                + "       r.review_content, "
                + "       TO_CHAR(r.review_date, 'YYYY-MM-DD') AS \"작성일\" "
                + "FROM   review r "
                + "       JOIN order_detail od ON r.order_detail_no = od.order_detail_no "
                + "       JOIN product p       ON od.product_no = p.product_no "
                + "       JOIN member m        ON r.member_no = m.member_no "
                + "WHERE  p.product_no = ? "
                + "ORDER  BY r.review_date DESC";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try {
            String productTable;
            boolean found;

            try (PreparedStatement stmt = conn.prepareStatement(productSql)) {
                stmt.setLong(1, productNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    found = rs.next();
                    productTable = found ? singleRowTableHtml(rs) : null;
                }
            }

            if (!found) {
                sendText(exchange, 404, "productNo=" + productNo + " 상품을 찾을 수 없습니다.");
                return;
            }

            String optionTable;
            try (PreparedStatement stmt = conn.prepareStatement(optionSql)) {
                stmt.setLong(1, productNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    optionTable = tableHtml(rs);
                }
            }

            String inquiryTable;
            try (PreparedStatement stmt = conn.prepareStatement(inquirySql)) {
                stmt.setLong(1, productNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    inquiryTable = inquiryAnswerHtml(rs);
                }
            }

            String reviewTable;
            try (PreparedStatement stmt = conn.prepareStatement(reviewSql)) {
                stmt.setLong(1, productNo);
                try (ResultSet rs = stmt.executeQuery()) {
                    reviewTable = tableHtml(rs);
                }
            }

            String body = "<h3>상품 정보</h3>" + productTable
                    + "<h3>옵션 목록</h3>" + optionTable
                    + "<h3>문의/답변 목록</h3>" + inquiryTable
                    + "<h3>리뷰 목록</h3>" + reviewTable;
            sendHtml(exchange, 200, renderPage("상품(" + productNo + ") 상세정보", body));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // ResultSet의 현재 커서 위치(rs.next() 호출 이후) 한 행만 세로형 key-value 테이블로 변환
    private static String singleRowTableHtml(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        StringBuilder html = new StringBuilder();
        html.append("<table>");
        for (int i = 1; i <= columnCount; i++) {
            html.append("<tr><th>").append(escapeHtml(meta.getColumnName(i))).append("</th>")
                    .append("<td>").append(escapeHtml(rs.getString(i))).append("</td></tr>");
        }
        html.append("</table>");
        return html.toString();
    }

    // 제목 + 본문(HTML)을 상단 네비게이션 바가 포함된 하나의 HTML 페이지로 감싸기
    private static String renderPage(String title, String bodyHtml) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"><style>")
                .append("table { border-collapse: collapse; }")
                .append("th, td { border: 1px solid #999; padding: 6px 12px; }")
                .append("th { background-color: #eee; }")
                .append("nav a, nav form { vertical-align: middle; }")
                .append("</style></head><body>");
        html.append(NAV_BAR);
        html.append("<h2>").append(escapeHtml(title)).append("</h2>");
        html.append(bodyHtml);
        html.append("</body></html>");
        return html.toString();
    }

    // GET /categorys -> 모든 카테고리(대/중/소분류)와 소분류별 상품 수 조회
    private static void handleCategoryList(HttpExchange exchange) throws IOException {
        String sql = "SELECT mc.main_category_no, "
                + "       mc.main_category_name, "
                + "       md.mid_category_no, "
                + "       md.mid_category_name, "
                + "       sc.sub_category_no, "
                + "       sc.sub_category_name, "
                + "       COUNT(p.product_no) AS product_count "
                + "FROM   MAIN_CATEGORY mc "
                + "       LEFT JOIN MID_CATEGORY md ON mc.main_category_no = md.main_category_no "
                + "       LEFT JOIN SUB_CATEGORY sc ON md.mid_category_no  = sc.mid_category_no "
                + "       LEFT JOIN PRODUCT p       ON sc.sub_category_no  = p.sub_category_no "
                + "GROUP  BY mc.main_category_no, mc.main_category_name, "
                + "          md.mid_category_no, md.mid_category_name, "
                + "          sc.sub_category_no, sc.sub_category_name "
                + "ORDER  BY mc.main_category_no, md.mid_category_no, sc.sub_category_no";

        Connection conn = DBConn.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            sendHtml(exchange, 200, renderPage("전체 카테고리 목록", categoryTableHtml(rs)));
        } catch (SQLException e) {
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        }
    }

    // 카테고리 목록을 HTML 테이블로 변환. 소분류 셀은 [7. 특정카테고리 상품조회](/category-products)로 연결되는 링크로 표시
    private static String categoryTableHtml(ResultSet rs) throws SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<table><tr>")
                .append("<th>대분류번호</th><th>대분류명</th>")
                .append("<th>중분류번호</th><th>중분류명</th>")
                .append("<th>소분류번호</th><th>소분류명</th>")
                .append("<th>상품수</th>")
                .append("</tr>");

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            String subCategoryNo = rs.getString("sub_category_no");

            html.append("<tr>");
            html.append("<td>").append(escapeHtml(rs.getString("main_category_no"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("main_category_name"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("mid_category_no"))).append("</td>");
            html.append("<td>").append(escapeHtml(rs.getString("mid_category_name"))).append("</td>");

            if (subCategoryNo == null) {
                html.append("<td></td><td></td>");
            } else {
                String link = "/category-products?categoryNo=" + subCategoryNo;
                html.append("<td><a href=\"").append(link).append("\">").append(escapeHtml(subCategoryNo)).append("</a></td>");
                html.append("<td><a href=\"").append(link).append("\">")
                        .append(escapeHtml(rs.getString("sub_category_name"))).append("</a></td>");
            }

            html.append("<td>").append(escapeHtml(rs.getString("product_count"))).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        if (rowCount == 0) {
            html.append("<p>조회된 데이터가 없습니다.</p>");
        }
        return html.toString();
    }

    // 문의(PRODUCT_INQUIRY) 1건 + 그 문의에 달린 답변(PRODUCT_INQUIRY_ANSWER) 여러 건을 묶어서 표시
    // (전제: rs가 po.OPTION_ID, pi.inquiry_no 순으로 정렬되어 있어 같은 inquiry_no 행이 연속으로 나옴)
    private static String inquiryAnswerHtml(ResultSet rs) throws SQLException {
        StringBuilder html = new StringBuilder();
        boolean any = false;

        String curInquiryNo = null;
        String curOptionId = null;
        String curInquiryContent = null;
        String curInquiryDate = null;
        String curMemberNo = null;
        StringBuilder curAnswers = new StringBuilder();

        while (rs.next()) {
            any = true;
            String inquiryNo = rs.getString("inquiry_no");

            if (!inquiryNo.equals(curInquiryNo)) {
                if (curInquiryNo != null) {
                    html.append(questionBlockHtml(curOptionId, curInquiryNo, curInquiryContent, curInquiryDate,
                            curMemberNo, curAnswers.toString()));
                }
                curInquiryNo = inquiryNo;
                curOptionId = rs.getString("OPTION_ID");
                curInquiryContent = rs.getString("inquiry_content");
                curInquiryDate = rs.getString("inquiry_date");
                curMemberNo = rs.getString("member_no");
                curAnswers = new StringBuilder();
            }

            if (rs.getString("answer_no") != null) {
                curAnswers.append(answerRowHtml(rs));
            }
        }

        if (curInquiryNo != null) {
            html.append(questionBlockHtml(curOptionId, curInquiryNo, curInquiryContent, curInquiryDate,
                    curMemberNo, curAnswers.toString()));
        }

        if (!any) {
            html.append("<p>조회된 데이터가 없습니다.</p>");
        }
        return html.toString();
    }

    private static String questionBlockHtml(String optionId, String inquiryNo, String inquiryContent,
            String inquiryDate, String memberNo, String answerRowsHtml) {
        StringBuilder html = new StringBuilder();
        html.append("<div style=\"border:1px solid #999; border-radius:6px; padding:10px; margin-bottom:14px;\">");
        html.append("<table style=\"margin-bottom:8px;\"><tr>")
                .append("<th>inquiry_no</th><td>").append(escapeHtml(inquiryNo)).append("</td>")
                .append("<th>option_id</th><td>").append(escapeHtml(optionId)).append("</td>")
                .append("<th>member_no</th><td>").append(escapeHtml(memberNo)).append("</td>")
                .append("<th>inquiry_date</th><td>").append(escapeHtml(inquiryDate)).append("</td>")
                .append("</tr></table>");
        html.append("<p><strong>Q.</strong> ").append(escapeHtml(inquiryContent)).append("</p>");
        html.append("<div style=\"margin-left:24px;\">");
        if (answerRowsHtml.isEmpty()) {
            html.append("<p style=\"color:#666;\">답변 없음</p>");
        } else {
            html.append("<table><tr><th>answer_no</th><th>answer_content</th><th>answer_date</th><th>seller_no</th></tr>")
                    .append(answerRowsHtml)
                    .append("</table>");
        }
        html.append("</div></div>");
        return html.toString();
    }

    private static String answerRowHtml(ResultSet rs) throws SQLException {
        return "<tr><td>" + escapeHtml(rs.getString("answer_no")) + "</td>"
                + "<td>" + escapeHtml(rs.getString("answer_content")) + "</td>"
                + "<td>" + escapeHtml(rs.getString("answer_date")) + "</td>"
                + "<td>" + escapeHtml(rs.getString("seller_no")) + "</td></tr>";
    }

    private static final Pattern OPTION_ROW_KEY_PATTERN = Pattern.compile("^opt_(\\d+)_");

    // GET /product-register -> 등록 폼 표시, POST /product-register -> 상품 + 옵션 저장
    private static void handleProductRegister(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            sendHtml(exchange, 200, renderPage("상품 등록", productRegisterFormHtml()));
        } else if ("POST".equalsIgnoreCase(method)) {
            handleProductRegisterSubmit(exchange);
        } else {
            sendText(exchange, 405, method + "는 지원하지 않습니다.");
        }
    }

    private static String productRegisterFormHtml() {
        return """
                <form method="post" action="/product-register">
                  <h3>상품 정보</h3>
                  <table>
                    <tr><th>상품명</th><td><input type="text" name="product_name" required></td></tr>
                    <tr><th>상품설명</th><td><textarea name="product_desc" required></textarea></td></tr>
                    <tr><th>가격</th><td><input type="number" name="product_price" required></td></tr>
                    <tr><th>수량</th><td><input type="number" name="quantity" value="0" required></td></tr>
                    <tr><th>판매자번호</th><td><input type="number" name="seller_no" required>
                        (<a href="/sellers" target="_blank">판매자 목록 보기</a>)</td></tr>
                    <tr><th>소분류번호</th><td><input type="number" name="sub_category_no" required>
                        (<a href="/categorys" target="_blank">카테고리 목록 보기</a>)</td></tr>
                  </table>

                  <h3>옵션 목록 (없어도 됩니다)</h3>
                  <div id="options"></div>
                  <button type="button" onclick="addOptionRow()">옵션 추가</button>

                  <p><button type="submit">상품 등록</button></p>
                </form>

                <script>
                  let optionRowIndex = 0;

                  function addOptionRow() {
                    const i = optionRowIndex++;
                    const div = document.createElement('div');
                    div.style.border = '1px solid #ccc';
                    div.style.padding = '8px';
                    div.style.marginBottom = '8px';
                    div.innerHTML =
                      '옵션1 <input name="opt_' + i + '_type1" placeholder="예: 색상" size="6">' +
                      '<input name="opt_' + i + '_value1" placeholder="예: 화이트" size="8"> ' +
                      '옵션2 <input name="opt_' + i + '_type2" placeholder="예: 사이즈" size="6">' +
                      '<input name="opt_' + i + '_value2" placeholder="예: 260" size="8"> ' +
                      '옵션3 <input name="opt_' + i + '_type3" placeholder="예: 소재" size="6">' +
                      '<input name="opt_' + i + '_value3" placeholder="예: 방수" size="8"> ' +
                      '개별가격 <input type="number" name="opt_' + i + '_price" placeholder="미입력시 상품가격" size="8"> ' +
                      '재고 <input type="number" name="opt_' + i + '_quantity" value="0" size="5"> ' +
                      '상태 <select name="opt_' + i + '_status"><option value="Y">판매중</option><option value="N">판매중지</option></select> ' +
                      '<button type="button" onclick="this.parentElement.remove()">삭제</button>';
                    document.getElementById('options').appendChild(div);
                  }
                </script>
                """;
    }

    private static void handleProductRegisterSubmit(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseFormBody(exchange);

        String productName = params.get("product_name");
        String productDesc = params.get("product_desc");
        Long productPrice = parseLongOrNull(params.get("product_price"));
        Long quantity = parseLongOrNull(params.get("quantity"));
        Long sellerNo = parseLongOrNull(params.get("seller_no"));
        Long subCategoryNo = parseLongOrNull(params.get("sub_category_no"));

        if (isBlank(productName) || isBlank(productDesc) || productPrice == null || quantity == null
                || sellerNo == null || subCategoryNo == null) {
            sendText(exchange, 400,
                    "product_name, product_desc, product_price, quantity, seller_no, sub_category_no는 모두 필수이며 숫자 항목은 숫자여야 합니다.");
            return;
        }

        List<OptionRow> optionRows = extractOptionRows(params);

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);

            String insertProductSql = "INSERT INTO PRODUCT "
                    + "(product_no, product_name, product_desc, product_price, quantity, seller_no, sub_category_no) "
                    + "VALUES (SEQ_PRODUCT.NEXTVAL, ?, ?, ?, ?, ?, ?)";

            long productNo;
            try (PreparedStatement stmt = conn.prepareStatement(insertProductSql, new String[] { "product_no" })) {
                stmt.setString(1, productName);
                stmt.setString(2, productDesc);
                stmt.setLong(3, productPrice);
                stmt.setLong(4, quantity);
                stmt.setLong(5, sellerNo);
                stmt.setLong(6, subCategoryNo);
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("등록된 product_no를 가져오지 못했습니다.");
                    }
                    productNo = keys.getLong(1);
                }
            }

            if (!optionRows.isEmpty()) {
                String insertOptionSql = "INSERT INTO PRODUCT_OPTION VALUES "
                        + "(SEQ_OPTION.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(insertOptionSql)) {
                    for (OptionRow row : optionRows) {
                        stmt.setLong(1, productNo);
                        setNullableString(stmt, 2, row.type1);
                        setNullableString(stmt, 3, row.value1);
                        setNullableString(stmt, 4, row.type2);
                        setNullableString(stmt, 5, row.value2);
                        setNullableString(stmt, 6, row.type3);
                        setNullableString(stmt, 7, row.value3);
                        if (row.price == null) {
                            stmt.setNull(8, Types.NUMERIC);
                        } else {
                            stmt.setLong(8, row.price);
                        }
                        stmt.setLong(9, row.quantity);
                        stmt.setString(10, row.status);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();

            String body = "<p>상품이 등록되었습니다. (product_no=" + productNo + ", 옵션 " + optionRows.size() + "개)</p>"
                    + "<p><a href=\"/product_detail?productNo=" + productNo + "\">등록된 상품 상세보기</a></p>";
            sendHtml(exchange, 200, renderPage("상품 등록 완료", body));
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                    // 롤백 실패는 원래 DB 오류를 그대로 보고하기 위해 무시
                }
            }
            sendText(exchange, 500, "DB 오류: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {
                    // 연결 종료 실패는 응답에 영향 없음
                }
            }
        }
    }

    // "opt_<번호>_..." 형식의 필드들을 번호별로 묶어서 옵션 행 목록으로 변환.
    // 타입/값/가격이 전부 비어 있는 행(사용자가 추가만 하고 채우지 않은 행)은 건너뜀
    private static List<OptionRow> extractOptionRows(Map<String, String> params) {
        TreeSet<Integer> indices = new TreeSet<>();
        for (String key : params.keySet()) {
            Matcher m = OPTION_ROW_KEY_PATTERN.matcher(key);
            if (m.find()) {
                indices.add(Integer.parseInt(m.group(1)));
            }
        }

        List<OptionRow> rows = new ArrayList<>();
        for (int i : indices) {
            String prefix = "opt_" + i + "_";
            String type1 = params.get(prefix + "type1");
            String value1 = params.get(prefix + "value1");
            String type2 = params.get(prefix + "type2");
            String value2 = params.get(prefix + "value2");
            String type3 = params.get(prefix + "type3");
            String value3 = params.get(prefix + "value3");
            Long price = parseLongOrNull(params.get(prefix + "price"));

            if (isBlank(type1) && isBlank(value1) && isBlank(type2) && isBlank(value2)
                    && isBlank(type3) && isBlank(value3) && price == null) {
                continue;
            }

            OptionRow row = new OptionRow();
            row.type1 = type1;
            row.value1 = value1;
            row.type2 = type2;
            row.value2 = value2;
            row.type3 = type3;
            row.value3 = value3;
            row.price = price;
            Long parsedQuantity = parseLongOrNull(params.get(prefix + "quantity"));
            row.quantity = parsedQuantity != null ? parsedQuantity : 0L;
            row.status = params.getOrDefault(prefix + "status", "Y");
            rows.add(row);
        }
        return rows;
    }

    private static Long parseLongOrNull(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static void setNullableString(PreparedStatement stmt, int index, String value) throws SQLException {
        if (isBlank(value)) {
            stmt.setNull(index, Types.VARCHAR);
        } else {
            stmt.setString(index, value);
        }
    }

    private static class OptionRow {
        String type1;
        String value1;
        String type2;
        String value2;
        String type3;
        String value3;
        Long price;
        long quantity;
        String status;
    }

    // ResultSet을 컬럼명 헤더가 있는 HTML 테이블 문자열로 변환
    private static String tableHtml(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        StringBuilder html = new StringBuilder();
        html.append("<table><tr>");
        for (int i = 1; i <= columnCount; i++) {
            html.append("<th>").append(escapeHtml(meta.getColumnName(i))).append("</th>");
        }
        html.append("</tr>");

        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            html.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                html.append("<td>").append(escapeHtml(rs.getString(i))).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");

        if (rowCount == 0) {
            html.append("<p>조회된 데이터가 없습니다.</p>");
        }
        return html.toString();
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static Map<String, String> parseQuery(URI uri) {
        return parseParams(uri.getQuery());
    }

    // POST 요청의 application/x-www-form-urlencoded 본문을 파라미터 맵으로 변환
    private static Map<String, String> parseFormBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return parseParams(body);
        }
    }

    private static Map<String, String> parseParams(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return result;
        }

        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx == -1)
                continue;
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            result.put(key, value);
        }
        return result;
    }

    private static void sendText(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}