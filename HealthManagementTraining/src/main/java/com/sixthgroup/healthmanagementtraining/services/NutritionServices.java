/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.TargetManagementController;
import com.sixthgroup.healthmanagementtraining.pojo.CalorieResult;
import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author PC
 */
public class NutritionServices {

    private static final double sedentaryCoefficient = 1.2;
    private static final double lightlyActiveCoefficient = 1.375;
    private static final double moderatelyActiveCoefficient = 1.55;
    private static final double veryActiveCoefficient = 1.725;
    private static final double extremelyActiveCoefficient = 1.9;

    private static final double maleWeightCoefficient = 13.7;
    private static final double femaleWeightCoefficient = 9.6;

    private static final double maleHeightCoefficient = 5;
    private static final double femaleHeightCoefficient = 1.8;

    private static final double maleAgeCoefficient = 6.8;
    private static final double femaleAgeCoefficient = 4.7;

    private static final double baseMaleBMR = 66;
    private static final double baseFemaleBMR = 655;

    private static final int caloriesPerWeight = 7700;

    private static final float baseFiber = 25f;
    private static final float baseProteinGainWeight = 0.2f;
    private static final float baseLipidGainWeight = 0.25f;

    private static final float baseProteinLossWeight = 0.25f;
    private static final float baseLipidLossWeight = 0.2f;
    private boolean bypassExerciseCheck = false; // Cờ kiểm tra

    public void setBypassExerciseCheck(boolean bypass) {
        this.bypassExerciseCheck = bypass;
    }

    public List<FoodCategory> getCates() throws SQLException {
        List<FoodCategory> cates = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM foodcategory");
//           
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                FoodCategory fc = new FoodCategory(rs.getInt("id"), rs.getString("categoryName"));
                cates.add(fc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cates;
    }

    public List<Food> getFoods(String kw) throws SQLException {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {

            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f "
                    + "JOIN foodcategory fc ON f.foodCategory_id = fc.id";
            PreparedStatement stm;
            if (kw != null && !kw.isEmpty()) { // Nếu có từ khóa, thêm điều kiện tìm kiếm
                sql += " WHERE f.foodName LIKE ?";
                stm = conn.prepareStatement(sql);
                stm.setString(1, "%" + kw + "%");
            } else { // Nếu không có từ khóa, lấy tất cả
                stm = conn.prepareStatement(sql);
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                // Lấy giá trị unitType từ CSDL dưới dạng chuỗi
                String unitTypeStr = rs.getString("unitType");

                // Chuyển đổi từ String thành Enum UnitType
                UnitType unitType = UnitType.valueOf(unitTypeStr);
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getFloat("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getInt("foodCategory_id"), // Lấy ID danh mục
                        rs.getString("categoryName"), // Lấy tên danh mục
                        unitType
                );
                foods.add(f);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public List<Food> getFoodsByCate(int cate_id) throws SQLException {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f "
                    + "JOIN foodcategory fc ON f.foodCategory_id = fc.id ";

            if (cate_id > 0) { // Nếu chọn danh mục cụ thể, thêm điều kiện lọc
                sql += "WHERE f.foodCategory_id = ?";
            }

            PreparedStatement stm = conn.prepareStatement(sql);
            if (cate_id > 0) {
                stm.setInt(1, cate_id);
            }

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UnitType unitType = UnitType.valueOf(rs.getString("unitType"));
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getInt("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getInt("foodCategory_id"),
                        rs.getString("categoryName"),
                        unitType
                );
                foods.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public List<Food> getFoodLogOfUser(String userId, LocalDate servingDate) throws SQLException {
        List<Food> selectedFoods = new ArrayList<>();
        String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                + "f.unitType, nl.numberOfUnit "
                + "FROM nutritionlog nl "
                + "JOIN food f ON nl.food_id = f.id "
                + "WHERE nl.userInfo_id = ? AND nl.servingDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(servingDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("Không có dữ liệu thức ăn đã chọn cho user " + userId);
                    return selectedFoods; // Trả về danh sách rỗng
                }

                while (rs.next()) {
                    Food food = new Food();
                    food.setId(rs.getInt("id"));
                    food.setFoodName(rs.getString("foodName"));
                    food.setCaloriesPerUnit(rs.getInt("caloriesPerUnit"));
                    food.setLipidPerUnit(rs.getFloat("lipidPerUnit"));
                    food.setProteinPerUnit(rs.getFloat("proteinPerUnit"));
                    food.setFiberPerUnit(rs.getFloat("fiberPerUnit"));
                    String unitTypeStr = rs.getString("unitType");
                    UnitType unitType = UnitType.valueOf(unitTypeStr);
                    food.setUnitType(unitType);
                    food.setSelectedQuantity(rs.getInt("numberOfUnit")); // Lưu số lượng đã chọn

                    selectedFoods.add(food);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectedFoods;
    }

    public void addFoodToLog(List<Food> selectedFoods, String userId, LocalDate servingDate) {
        String insertSql = "INSERT INTO nutritionlog (numberOfUnit, servingDate, food_id, userInfo_id)"
                + " VALUES (?, ?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            int insertedCount = 0;

            for (Food food : selectedFoods) {
                // Sử dụng phương thức kiểm tra
                if (!bypassExerciseCheck && isFoodAlreadyLogged(userId, servingDate, food.getId())) {
                    continue; // Nếu món ăn đã có, bỏ qua
                }
                insertStmt.setInt(1, food.getSelectedQuantity());
                insertStmt.setDate(2, Date.valueOf(servingDate));
                insertStmt.setInt(3, food.getId());
                insertStmt.setString(4, userId);
                insertStmt.addBatch();
                insertedCount++;
            }

            if (insertedCount > 0) {
                insertStmt.executeBatch(); // Thực hiện thêm tất cả món ăn mới
            } else {
//                Utils.showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Tất cả món ăn đã có trong nhật ký.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
//            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi lưu dữ liệu!");
        }
    }

    public void deleteFoodFromLog(int foodId, String userId, LocalDate servingDate) throws SQLException {
        String sql = "DELETE FROM nutritionlog WHERE food_id = ? AND userInfo_id = ? AND servingDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, foodId);
            pstmt.setString(2, userId);
            pstmt.setDate(3, Date.valueOf(servingDate));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Không có dữ liệu nào bị xóa!");
            } else {
//                Utils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa món ăn khỏi nhật kí");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public float getDailyCaloNeeded(String username, LocalDate currentDate) {
        String sql = "SELECT g.dailyCaloNeeded FROM goal g JOIN userinfo u ON g.userInfo_id = u.id WHERE userName = ? AND ? BETWEEN startDate AND endDate LIMIT 1";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setDate(2, Date.valueOf(currentDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("dailyCaloNeeded");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    //lấy mục tiêu hiện tại
    public static Goal getCurrentGoal(String userInfoId,LocalDate currentDate) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT * FROM goal WHERE userInfo_id = ? AND ? BETWEEN startDate AND endDate";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userInfoId);
            stmt.setDate(2, Date.valueOf(currentDate));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Goal goal = new Goal(
                        rs.getInt("id"),
                        rs.getFloat("targetWeight"),
                        rs.getFloat("currentWeight"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getFloat("dailyCaloNeeded"),
                        rs.getInt("currentProgress")
                );
                goal.setUserInfoId(userInfoId);
                return goal;
            }
        }
        return null; // Không có goal nào đang hoạt động

    }
    public CalorieResult calCaloriesNeeded(String username, float targetWeight, float currentWeight, LocalDate startDate, LocalDate endDate) throws SQLException {
        TargetManagementController tc = new TargetManagementController();
        if (tc.checkGoal(String.valueOf(targetWeight), String.valueOf(currentWeight), startDate, endDate)) {
            UserInfoServices s = new UserInfoServices();
            UserInfo u = s.getUserInfo(username);
            double BMR;
            int age = calculateAge((Date) u.getDOB());
            System.out.println("age: " + age);
            if (u.getGender().equalsIgnoreCase("Nam")) {
                BMR = baseMaleBMR + (maleWeightCoefficient * currentWeight)
                        + (maleHeightCoefficient * u.getHeight()) - (maleAgeCoefficient * calculateAge((Date) u.getDOB()));
            } else {
                BMR = baseFemaleBMR + (femaleWeightCoefficient * currentWeight)
                        + (femaleHeightCoefficient * u.getHeight()) - (femaleAgeCoefficient * calculateAge((Date) u.getDOB()));
            }
            System.out.println("BMR: " + BMR);
            float activityLevel = Utils.parseDoubleToFloat(getActivityCoefficient(u.getActivityLevel()), 3);
            float TDEE = Utils.roundFloat(Utils.parseDoubleToFloat(BMR, 2) * activityLevel, 3);
            System.out.println("TDEE: " + TDEE);
            float weightChange = targetWeight - currentWeight;
            System.out.println("weightChange: " + weightChange);
            float totalCaloriesNeeded = weightChange * caloriesPerWeight;
            System.out.println("totalCaloriesNeeded: " + totalCaloriesNeeded);
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            System.out.println("totalDays : " + totalDays);
            if (totalDays <= 0) {
                throw new IllegalArgumentException("End date must be after start date.");
            }

            float dailyCalorieChange = totalCaloriesNeeded / totalDays;
//        System.out.println("dailyCalorieChange: " + dailyCalorieChange);
            float dailyCalorieIntake = Utils.roundFloat(TDEE + dailyCalorieChange, 1);
            System.out.println("dailyCalorieIntake: " + dailyCalorieIntake);

            String targetType = currentWeight > targetWeight ? "loss" : "gain";
            float dailyProteinIntake;
            float dailyLipidIntake;
            if (targetType.equalsIgnoreCase("loss")) {
                dailyProteinIntake = Utils.roundFloat(TDEE * baseProteinLossWeight, 1);
                System.out.println("dailyProteinIntake " + dailyProteinIntake);

                dailyLipidIntake = Utils.roundFloat(TDEE * baseLipidLossWeight, 1);
                System.out.println("dailyLipidIntake " + dailyLipidIntake);
            } else {
                dailyProteinIntake = Utils.roundFloat(TDEE * baseProteinGainWeight, 1);
                dailyLipidIntake = Utils.roundFloat(TDEE * baseLipidGainWeight, 1);
            }

            return new CalorieResult(dailyCalorieIntake, dailyCalorieChange, dailyProteinIntake, dailyLipidIntake, Utils.convertToFloat(baseFiber));
        }
        return null;
    }

    public double getActivityCoefficient(String activityLevel) {
        if (activityLevel == null) {
            throw new IllegalArgumentException("Activity level must not be null");
        }
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                return sedentaryCoefficient;
            case "lightlyactive":
                return lightlyActiveCoefficient;
            case "moderatelyactive":
                return moderatelyActiveCoefficient;
            case "veryactive":
                return veryActiveCoefficient;
            case "extremelyactive":
                return extremelyActiveCoefficient;
            default:
                throw new IllegalArgumentException("Invalid activity level");
        }
    }

    public static int calculateAge(Date dob) {
        int age = 0;

        if (dob == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Ngày sinh không được để trống");
            return age;
        }

        if (isDobInFuture(dob)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Ngày sinh không hợp lệ (trong tương lai)");
            return age;
        }

        try {
            age = calculateAgeRaw(dob); // ✅ gọi hàm mới, tránh vòng lặp
        } catch (IllegalArgumentException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
            return age;
        }

        if (!isAgeEligible(age)) {
            Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Chỉ cho phép người dùng từ 16 đến 59 tuổi");
            return 0;
        }

        return age;
    }

    // HÀM TÍNH TUỔI THỰC SỰ, KHÔNG GỒM RÀNG BUỘC
    private static int calculateAgeRaw(Date dob) {
        LocalDate birthDate;
        if (dob instanceof java.sql.Date) {
            birthDate = ((java.sql.Date) dob).toLocalDate();
        } else {
            birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public static boolean isAgeEligible(int age) {
        return age >= 16 && age <= 59;
    }

    public static boolean isDobInFuture(Date dob) {
        LocalDate birthDate;
        if (dob instanceof java.sql.Date) {
            birthDate = ((java.sql.Date) dob).toLocalDate();
        } else {
            birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return birthDate.isAfter(LocalDate.now());
    }

    public float calTotalCalories(List<Food> listFood) {
        float total = 0;
        for (Food food : listFood) {
            total += food.getCaloriesPerUnit() * food.getSelectedQuantity();
        }
        return total;
    }

    public boolean isValidInput(String inputQuantity, String unitType) {
        int minQuantity = 0;
        int maxQuantity = 0;
        try {
            int input = Integer.parseInt(inputQuantity);
            if (input < 0) {
                Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập số nguyên dương");
                return false;
            }
            if (unitType.equals("gram")) {
                minQuantity = 50;
                maxQuantity = 300;

                if (input >= minQuantity && input <= maxQuantity) {
                    return true;
                } else {
                    Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", " Khối lượng món ăn phải từ 50 đến 300 gram !");
                    return false;
                }
            } else if (unitType.endsWith("ml")) {
                minQuantity = 200;
                maxQuantity = 500;

                if (input >= minQuantity && input <= maxQuantity) {
                    return true;
                } else {
                    Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", " Khối lượng món ăn phải từ 200 đến 500 ml !");
                    return false;
                }
            } else {
                minQuantity = 10;
                maxQuantity = 20;
                if (input >= minQuantity && input <= maxQuantity) {
                    return true;
                } else {
                    Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", " Khối lượng món ăn phải từ 10 đến 20 miếng !");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập một số nguyên hợp lệ!");
//            e.printStackTrace();
            return false;
        }
    }

    public boolean isExistFood(List<Food> selectedFoods, Food currentFood) {

        for (Food f : selectedFoods) {
            if (currentFood.getFoodName().equals(f.getFoodName())) {
                return true;
            }
        }
        return false; // Trả về false nếu không bị trùng
    }

    public boolean isPositiveCalories(float calories) {
        return calories > 0;
    }

    public boolean isFoodAlreadyLogged(String userId, LocalDate servingDate, int foodId) {
        String checkSql = "SELECT COUNT(*) FROM nutritionlog WHERE servingDate = ? AND userInfo_id = ? AND food_id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setDate(1, Date.valueOf(servingDate));
            checkStmt.setString(2, userId);
            checkStmt.setInt(3, foodId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Nếu COUNT > 0 -> Món ăn đã tồn tại
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Trả về false nếu có lỗi xảy ra
    }
}
