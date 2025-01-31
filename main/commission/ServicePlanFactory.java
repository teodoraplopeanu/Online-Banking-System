package org.poo.main.commission;

public final class ServicePlanFactory {
    private ServicePlanFactory() { }

    /**
     * Returns a custom service plan object
     * according to the type given as input
     * */
    public static CommissionStrategy chooseServicePlan(final String type) {
        return switch (type) {
            case "standard" -> new Standard();
            case "student" -> new Student();
            case "silver" -> new Silver();
            case "gold" -> new Gold();
            default -> null;
        };
    }
}
