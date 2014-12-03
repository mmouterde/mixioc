package net.mixioc.internal;

public class ServiceRegistration {

    private Object service;
    private String parameters;

    public ServiceRegistration(Object service, String parameters) {
        this.service = service;
        this.parameters = parameters;
    }

    public Object getService() {
        return service;
    }

    public String getParameters() {
        return parameters;
    }

    public boolean matchs(String contraints) {
        return computeMatcher(contraints);
    }

    private boolean computeMatcher(String contraints) {
        String[] contraintsList = contraints.split(",");
        for (String filter : contraintsList) {
            if (!("," + parameters + ",").contains("," + filter + ",")) {
                return false;
            }
        }
        return true;
    }
}
