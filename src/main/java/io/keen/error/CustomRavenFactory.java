package io.keen.error;

import net.kencochrane.raven.DefaultRavenFactory;
import net.kencochrane.raven.Raven;
import net.kencochrane.raven.dsn.Dsn;
import net.kencochrane.raven.marshaller.Marshaller;
import net.kencochrane.raven.marshaller.json.JsonMarshaller;

// This custom factor is only here to override createMarshalle
// below
public class CustomRavenFactory extends DefaultRavenFactory {
    @Override
    public Raven createRavenInstance(Dsn dsn) {
      Raven raven = new Raven();
      raven.setConnection(createConnection(dsn));

      return raven;
    }

    // This method adds an interface for PlayRequestInterface so that we can serialize
    // the request!
    @Override
    protected Marshaller createMarshaller(Dsn dsn) {
      JsonMarshaller marshaller = (JsonMarshaller) super.createMarshaller(dsn);

      marshaller.addInterfaceBinding(PlayRequestInterface.class, new PlayRequestInterfaceBinding());

      return marshaller;
    }
}
