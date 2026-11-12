package dev.bjervig.meeting_transcribe_api.model;

public enum AnalysisProfile {

    MEETING_MINUTES("meeting-minutes",   0.1, "prompts/meeting-minutes.txt"),
    BRAINSTORM("brainstorm",             0.4, "prompts/brainstorm.txt"),
    TECHNICAL_REVIEW("technical-review", 0.1, "prompts/technical-review.txt"),
    INTERVIEW("interview",              0.2, "prompts/interview.txt");

    private final String id;
    private final double temperature;
    private final String promptResource;

    AnalysisProfile(String id, double temperature, String promptResource) {
        this.id = id;
        this.temperature = temperature;
        this.promptResource = promptResource;
    }

    public String getId() { return id; }
    public double getTemperature() { return temperature; }
    public String getPromptResource() { return promptResource; }
}
