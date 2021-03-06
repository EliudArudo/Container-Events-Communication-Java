package util;

import dockerapi.ContainerInfo;
import interfaces.ContainerInfoInterface;
import interfaces.ReceivedEventInterface;
import interfaces.STATUS_TYPE;
import log.Logging;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Util {
    private static String packageName = "util::Util";

    private static ArrayList<String> responseBuffer = new ArrayList();
    private static ArrayList<ReceivedEventInterface> responses = new ArrayList();

    public Util () {}

    public static ArrayList<String> getResponseBuffer() {
       return (ArrayList<String>) responseBuffer.clone();
    }

    public static ArrayList<ReceivedEventInterface> getResponses () {
        return (ArrayList<ReceivedEventInterface>) responses.clone();
    }

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

        try {
            ReceivedEventInterface response = new ReceivedEventInterface();

            String receivedEventId = responseBuffer
                    .stream()
                    .filter(event -> event.equals(requestId))
                    .findAny()
                    .orElse(null);

            boolean responseArrived = receivedEventId != null;
            if(responseArrived) {
                ReceivedEventInterface _response = responses.stream()
                        .filter(foundResponse -> foundResponse.requestId.equals(requestId))
                        .findAny()
                        .orElse(null);

                response = (ReceivedEventInterface) _response.clone();
                clearResponseFromBuffers(response);
            }

            return response;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getResponseFromBuffer", e.getMessage());
            return null;
        }
    }

    public static ContainerInfoInterface getSelectedEventContainerIdAndService() {

        try {
            ArrayList<ContainerInfoInterface> containers = new ContainerInfo().getFreshContainers();

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

            while(selectedContainer.id.equals(""))
                getSelectedEventContainerIdAndService();

            return selectedContainer;
        } catch(Exception e) {
            Logging.logStatusFileMessage(STATUS_TYPE.Failure, packageName, "getSelectedEventContainerIdAndService", e.getMessage());
            return null;
        }
    }
}
