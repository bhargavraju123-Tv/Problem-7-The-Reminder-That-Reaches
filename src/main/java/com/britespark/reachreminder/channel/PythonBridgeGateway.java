package com.britespark.reachreminder.channel;

import com.britespark.reachreminder.config.ReminderProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class PythonBridgeGateway implements ChannelGateway {

    private final String pythonCmd;
    private final ObjectMapper mapper = new ObjectMapper();

    public PythonBridgeGateway(ReminderProperties props) {
        this.pythonCmd = props.getPythonCommand();
    }

    @Override
    public ChannelResult send(String channel, String to, String body, int attempt) {
        try {
            // This launches your bridge.py script
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCmd, "bridge.py", channel, to, body, String.valueOf(attempt)
            );
            Process p = pb.start();

            // Read the JSON response from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String output = reader.readLine();
            p.waitFor();

            if (output == null || output.isBlank()) {
                return new ChannelResult("system_failure", "Python script returned no output");
            }

            // Convert the JSON string back into our Java ChannelResult object
            JsonNode node = mapper.readTree(output);
            return new ChannelResult(node.get("status").asText(), node.get("detail").asText());

        } catch (Exception e) {
            return new ChannelResult("system_failure", e.getMessage());
        }
    }
}