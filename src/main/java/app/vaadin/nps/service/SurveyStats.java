package app.vaadin.nps.service;

public record SurveyStats(double npsScore,
                          long promoters,
                          long passives,
                          long detractors,
                          long totalSent) {

    /**
     * Returns the total number of responses
     */
    public long totalResponses() {
        return promoters + passives + detractors;
    }

    /**
     * Returns the response rate as a percentage
     */
    public double responseRate() {
        if (totalSent == 0) return 0.0;
        return (double) totalResponses() / totalSent * 100;
    }

    /**
     * Returns the percentage of promoters among respondents
     */
    public double promoterPercentage() {
        long total = totalResponses();
        if (total == 0) return 0.0;
        return (double) promoters / total * 100;
    }

    /**
     * Returns the percentage of passives among respondents
     */
    public double passivePercentage() {
        long total = totalResponses();
        if (total == 0) return 0.0;
        return (double) passives / total * 100;
    }

    /**
     * Returns the percentage of detractors among respondents
     */
    public double detractorPercentage() {
        long total = totalResponses();
        if (total == 0) return 0.0;
        return (double) detractors / total * 100;
    }

    /**
     * Returns the count of pending responses
     */
    public long pendingResponses() {
        return totalSent - totalResponses();
    }

    /**
     * Factory method for creating an empty stats object
     */
    public static SurveyStats empty() {
        return new SurveyStats(0.0, 0, 0, 0, 0);
    }

    /**
     * Validate the record's invariants
     */
    public SurveyStats {
        if (totalSent < 0) {
            throw new IllegalArgumentException("Total sent cannot be negative");
        }
        if (promoters < 0 || passives < 0 || detractors < 0) {
            throw new IllegalArgumentException("Response counts cannot be negative");
        }
        if (promoters + passives + detractors > totalSent) {
            throw new IllegalArgumentException("Total responses cannot exceed total sent");
        }
    }
}