package logic;

import interfaces.ReceivedEventInterface;
import interfaces.SUB_TASK_TYPE;
import interfaces.TASK_TYPE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LogicTest {

    String stringRequestBody = "{ \"a1\": \"a\", \"a2\": \"b\"}";
    String string1 = "a";
    String string2 = "b";

    String numberRequestBody = "{ \"a1\": \"1\", \"a2\": \"2\"}";
    String number1 = "1";
    String number2 = "2";

    String expectedStringAddResult = string1 + string2;

    Double numberAdditionResult = (Double.parseDouble(number1) + Double.parseDouble(number2));
    Double numberSubtractionResult = (Double.parseDouble(number1) - Double.parseDouble(number2));
    Double numberMultiplicationResult = (Double.parseDouble(number1) * Double.parseDouble(number2));
    Double numberDivisionResult = (Double.parseDouble(number1) / Double.parseDouble(number2));

    String expectedNumberAdditionResult = numberAdditionResult.toString();
    String expectedNumberSubtractionResult = numberSubtractionResult.toString();
    String expectedNumberMultiplicationResult = numberMultiplicationResult.toString();
    String expectedNumberDivisionResult = numberDivisionResult.toString();

    ReceivedEventInterface task = new ReceivedEventInterface();

    @Test
    public void TestDevAddStrings() {
        task.task = TASK_TYPE.STRING;
        task.subtask = SUB_TASK_TYPE.ADD;
        task.requestBody = stringRequestBody;

        String unitResult = Logic.devAddStrings(string1, string2);
        String integratedResult = Logic.performLogic(task);

        assertEquals(expectedStringAddResult, unitResult);
        assertEquals(expectedStringAddResult, integratedResult);
    }

    @Test
    public void TestUnitDevAddNumber() {
        task.task = TASK_TYPE.NUMBER;
        task.subtask = SUB_TASK_TYPE.ADD;
        task.requestBody = numberRequestBody;

        String result = Logic.devAddNumber(number1, number2);
        String integratedResult = Logic.performLogic(task);

        assertEquals(expectedNumberAdditionResult, result);
        assertEquals(expectedNumberAdditionResult, integratedResult);
    }

    @Test
    public void TestUnitDevSubtractNumber() {
        task.task = TASK_TYPE.NUMBER;
        task.subtask = SUB_TASK_TYPE.SUBTRACT;
        task.requestBody = numberRequestBody;

        String result = Logic.devSubtractNumber(number1, number2);
        String integratedResult = Logic.performLogic(task);

        assertEquals(expectedNumberSubtractionResult, result);
        assertEquals(expectedNumberSubtractionResult, integratedResult);
    }

    @Test
    public void TestUnitDevMultiplyNumber() {
        task.task = TASK_TYPE.NUMBER;
        task.subtask = SUB_TASK_TYPE.MULTIPLY;
        task.requestBody = numberRequestBody;

        String result = Logic.devMultiplyNumber(number1, number2);
        String integratedResult = Logic.performLogic(task);

        assertEquals(expectedNumberMultiplicationResult, result);
        assertEquals(expectedNumberMultiplicationResult, integratedResult);
    }

    @Test
    public void TestUnitDevDivideNumber() {
        task.task = TASK_TYPE.NUMBER;
        task.subtask = SUB_TASK_TYPE.DIVIDE;
        task.requestBody = numberRequestBody;

        String result = Logic.devDivideNumber(number1, number2);
        String integratedResult = Logic.performLogic(task);

        assertEquals(expectedNumberDivisionResult, result);
        assertEquals(expectedNumberDivisionResult, integratedResult);
    }

}
