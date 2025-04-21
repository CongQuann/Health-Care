//
//import com.sixthgroup.healthmanagementtraining.SecondaryController;
//import com.sixthgroup.healthmanagementtraining.services.Utils;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.stream.Stream;
//import javafx.scene.control.Alert;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
///**
// *
// * @author quanp
// */
//public class SecondaryTest {
//    private static Stream<Arguments> provideLoginInputs() {
//        LocalDate today = LocalDate.now();
//        return Stream.of(
//                Arguments.of("", "password", false),                            // thiếu username
//                Arguments.of("username", "", false),                            // thiếu password
//                Arguments.of("", "", false),                                    // thiếu username và passsword
//                Arguments.of("username", "password", true)                      // đầy đủ
//
//        );
//    }
//     @ParameterizedTest
//    @MethodSource("provideLoginInputs")
//    public void testCheckGoalInputs(String username, String password, boolean expectedResult) throws SQLException {
//        SecondaryController controller = new SecondaryController();
//        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
//            mockedUtils.when(() -> Utils.getAlert(anyString()))
//                       .thenReturn(mock(Alert.class));
//            boolean result = controller.checkLogin(username, password);
//            assertEquals(expectedResult, result);
//    }
//    }
//}
