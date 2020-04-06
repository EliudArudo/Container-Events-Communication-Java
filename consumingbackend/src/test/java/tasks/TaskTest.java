package tasks;

import dockerapi.ContainerInfo;
import interfaces.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TaskTest {
    String stringsAddRequestBody = "{ \"a1\": \"a\", \"a2\": \"b\" }";
    String numbersAddRequestBody = "{ \"a1\": \"4\", \"a2\": \"5\" }";
    String numbersSubtractRequestBody = "{ \"s1\": \"4\", \"s2\": \"5\" }";
    String numbersMultiplyRequestBody = "{ \"m1\": \"4\", \"m2\": \"5\" }";
    String numbersDivideRequestBody = "{ \"d1\": \"4\", \"d2\": \"5\" }";

    String invalidRequestBody = "";


    ContainerInfoInterface dummyContainerInfo = new ContainerInfoInterface("dummyContainerId", "dummyContainerService");
    ContainerInfoInterface dummyEventContainerInfo = new ContainerInfoInterface("dummyEventContainerId", "dummyEventContainerService");

    @Mock
    ContainerInfo containerInfo;


    @Test
    public void TestDetermineStringTask() {
        TASK_TYPE task = Task.determineTask(stringsAddRequestBody);

        assertEquals(TASK_TYPE.STRING, task);
    }

    @Test
    public void TestDetermineNumberTask() {
        TASK_TYPE task = Task.determineTask(numbersAddRequestBody);

        assertEquals(TASK_TYPE.NUMBER, task);
    }

    @Test
    public void DetermineSubTaskReturnsStringAdd() {
        TASK_TYPE task = Task.determineTask(stringsAddRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, stringsAddRequestBody);

        assertEquals(TASK_TYPE.STRING, task);
        assertEquals(SUB_TASK_TYPE.ADD, subtask);
    }

    @Test
    public void DetermineSubTaskReturnsNumberAdd() {
        TASK_TYPE task = Task.determineTask(numbersAddRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, numbersAddRequestBody);

        assertEquals(TASK_TYPE.NUMBER, task);
        assertEquals(SUB_TASK_TYPE.ADD, subtask);
    }

    @Test
    public void DetermineSubTaskReturnsNumberSubtract() {
        TASK_TYPE task = Task.determineTask(numbersSubtractRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, numbersSubtractRequestBody);

        assertEquals(TASK_TYPE.NUMBER, task);
        assertEquals(SUB_TASK_TYPE.SUBTRACT, subtask);
    }

    @Test
    public void DetermineSubTaskReturnsNumberMultiply() {
        TASK_TYPE task = Task.determineTask(numbersMultiplyRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, numbersMultiplyRequestBody);

        assertEquals(TASK_TYPE.NUMBER, task);
        assertEquals(SUB_TASK_TYPE.MULTIPLY, subtask);
    }

    @Test
    public void DetermineSubTaskReturnsNumberDivide() {
        TASK_TYPE task = Task.determineTask(numbersDivideRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, numbersDivideRequestBody);

        assertEquals(TASK_TYPE.NUMBER, task);
        assertEquals(SUB_TASK_TYPE.DIVIDE, subtask);
    }


    @Test(expected = Exception.class)
    public void TaskDeterminerThrowsOnInvalidTask() {
        TASK_TYPE task = Task.determineTask(invalidRequestBody);
        SUB_TASK_TYPE subtask = Task.determineSubTask(task, invalidRequestBody);

        Task.taskDeterminer(task, subtask, stringsAddRequestBody, containerInfo);
    }

}
