package be.vinci.pae.domain;

import be.vinci.pae.services.AvailableDAO;
import be.vinci.pae.services.AvailableDAOImpl;
import be.vinci.pae.services.DALBackendServicesImpl;
import be.vinci.pae.services.DALServices;
import be.vinci.pae.services.FilterDAO;
import be.vinci.pae.services.FilterDAOImpl;
import be.vinci.pae.services.NotificationDAO;
import be.vinci.pae.services.NotificationDAOImpl;
import be.vinci.pae.services.ObjectDAO;
import be.vinci.pae.services.ObjectDAOImpl;
import be.vinci.pae.services.UserDAO;
import be.vinci.pae.services.UserDAOImpl;
import be.vinci.pae.utils.ApplicationBinder;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.mockito.Mockito;

/**
 * TestBinder class.
 */
@Provider
public class TestBinder extends ApplicationBinder {

  /**
   * Configure the bindings.
   */
  @Override
  protected void configure() {
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(Mockito.mock(UserDAOImpl.class)).to(UserDAO.class);

    bind(Mockito.mock(DALBackendServicesImpl.class)).to(DALServices.class);

    bind(ObjectFactoryImpl.class).to(ObjectFactory.class).in(Singleton.class);
    bind(ObjectUCCImpl.class).to(ObjectUCC.class).in(Singleton.class);
    bind(Mockito.mock(ObjectDAOImpl.class)).to(ObjectDAO.class);

    bind(FilterFactoryImpl.class).to(FilterFactory.class).in(Singleton.class);
    bind(FilterUCCImpl.class).to(FilterUCC.class).in(Singleton.class);
    bind(Mockito.mock(FilterDAOImpl.class)).to(FilterDAO.class);

    bind(NotificationFactoryImpl.class).to(NotificationFactory.class).in(Singleton.class);
    bind(NotificationUCCImpl.class).to(NotificationUCC.class).in(Singleton.class);
    bind(Mockito.mock(NotificationDAOImpl.class)).to(NotificationDAO.class);

    bind(AvailableFactoryImpl.class).to(AvailableFactory.class).in(Singleton.class);
    bind(AvailableUCCImpl.class).to(AvailableUCC.class).in(Singleton.class);
    bind(Mockito.mock(AvailableDAOImpl.class)).to(AvailableDAO.class);

  }
}