package robot_dreams.aws.lambdas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

class GPAHandlerTest {

    @Test
    void testAverageGpaWithAllSameValues() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, 3.5),
                new Data(1300, 3.5),
                new Data(1400, 3.5)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(3.5, result, "Average GPA should be 3.5 when all values are the same.");
    }

    @Test
    void testAverageGpaWithLargeInputList() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            dataList.add(new Data(1200 + i, 3.8));
        }

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(3.8, result, "Average GPA for a large list of the same value should be 3.8.");
    }

    @Test
    void testAverageGpaWithExtremeValues() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, 0.0),
                new Data(1300, 5.0),
                new Data(1400, 2.5)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(2.5, result, "Average GPA should handle extreme values correctly.");
    }

    @Test
    void testAverageGpaWithPositiveValues() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, 3.5),
                new Data(1300, 3.8),
                new Data(1400, 4.0)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(3.77, result, "Average GPA should be correctly rounded.");
    }

    @Test
    void testAverageGpaWithMixedValues() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, 2.7),
                new Data(1300, 3.3),
                new Data(1400, 4.0)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(3.33, result, "Average GPA should be correctly rounded.");
    }
    
    @Test
    void testAverageGpaWithEmptyInputFile() throws IOException {
        GPAHandler handler = new GPAHandler();
        
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/empty_input.txt"))) {
            List<Data> dataList = handler.convertData(inputStream, ',', true);
        
            double result = handler.averageGpa(dataList);
        
            Assertions.assertEquals(0.0, result, "Average GPA of an empty input file should be 0.0.");
        }
    }
    
    @Test
    void testAverageGpaWithInputFile() throws IOException {
        GPAHandler handler = new GPAHandler();
    
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/input.txt"))) {
            List<Data> dataList = handler.convertData(inputStream, ',', true);

            double result = handler.averageGpa(dataList);
        
            Assertions.assertEquals(3.33, result, "Average GPA calculated from input.txt should be 3.33.");
        }
    }

    @Test
    void testAverageGpaWithSingleValue() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(new Data(1500, 3.9));

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(3.9, result, "Average GPA of a single value should be correctly rounded.");
    }

    @Test
    void testAverageGpaWithEmptyList() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of();

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(0.0, result, "Average GPA of an empty list should be 0.0.");
    }

    @Test
    void testAverageGpaWithNegativeValues() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, -1.2),
                new Data(1300, -3.4)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(-2.3, result, "Average GPA with negative values should be correctly rounded.");
    }

    @Test
    void testAverageGpaWithZeroGPA() {
        GPAHandler handler = new GPAHandler();

        List<Data> dataList = List.of(
                new Data(1200, 0.0),
                new Data(1300, 0.0)
        );

        double result = handler.averageGpa(dataList);

        Assertions.assertEquals(0.0, result, "Average GPA with zero values should be 0.0.");
    }
}