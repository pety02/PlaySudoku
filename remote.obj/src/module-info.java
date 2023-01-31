module remote.obj {
    requires java.rmi;
    requires java.logging;

    exports server.entities;
    exports server.services;
    exports server.servicesImpls;
}