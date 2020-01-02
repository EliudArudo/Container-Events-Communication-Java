package util;

import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.ReceivedEventInterface;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Util {

    private static ArrayList<String> responseBuffer = new ArrayList();
    private static ArrayList<ReceivedEventInterface> responses = new ArrayList();

    public static void pushResponseToBuffers(ReceivedEventInterface response) {
        responseBuffer.add(response.requestId);
        responses.add(response);
    }

    public static void clearResponseFromBuffers(ReceivedEventInterface response) {
       responseBuffer = (ArrayList<String>) responseBuffer.stream()
               .filter(res -> res != response.requestId)
               .collect(Collectors.toList());

       responses = (ArrayList<ReceivedEventInterface>) responses.stream()
               .filter(foundResponse -> foundResponse.requestId != response.requestId)
               .collect(Collectors.toList());
    }

    public static ReceivedEventInterface getResponseFromBuffer(String requestId) {

        ReceivedEventInterface response = new ReceivedEventInterface();
        try {
            String receivedEventId = responseBuffer
                    .stream()
                    .filter(event -> event.equals(requestId))
                    .findAny()
                    .orElse(null);

            boolean responseArrived = receivedEventId != null;


            if(responseArrived) {
                ReceivedEventInterface _response = responses.stream()
                        .filter(foundResponse -> foundResponse.requestId == requestId)
                        .findAny()
                        .orElse(null);

                response = (ReceivedEventInterface) _response.clone();
                clearResponseFromBuffers(response);
            }

        } catch(CloneNotSupportedException e) {
            System.out.printf("Cannot clone _response object %s", e.getMessage());
        } finally {
           return response;
        }
    }

    public static ContainerInfoInterface getSelectedEventContainerIdAndService() {
        ArrayList<ContainerInfoInterface> containers = ContainerInfo.getFreshContainers();

        ArrayList<ContainerInfoInterface> selectedContainers = new ArrayList();

        final String selectedService = "event"; // 'eventsService'
        for(ContainerInfoInterface container: containers) {
            final String lowerCaseContainerService =
                    container.service.toLowerCase();
            final boolean containerBelongsToSelectedService =
                    lowerCaseContainerService.contains(selectedService);

            if(containerBelongsToSelectedService)
                selectedContainers.add(container);
        }

        int randomIndex = (int) Math.floor(Math.random() * selectedContainers.size());

        ContainerInfoInterface selectedContainer = selectedContainers.get(randomIndex);

        while(selectedContainer.id == "")
            getSelectedEventContainerIdAndService();

        return selectedContainer;
    }
}
