package be.vinci.pae.utils;

import be.vinci.pae.domain.AvailableFactory;
import be.vinci.pae.domain.AvailableFactoryImpl;
import be.vinci.pae.domain.AvailableUCC;
import be.vinci.pae.domain.AvailableUCCImpl;
import be.vinci.pae.domain.FilterFactory;
import be.vinci.pae.domain.FilterFactoryImpl;
import be.vinci.pae.domain.FilterUCC;
import be.vinci.pae.domain.FilterUCCImpl;
import be.vinci.pae.domain.NotificationFactory;
import be.vinci.pae.domain.NotificationFactoryImpl;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.domain.NotificationUCCImpl;
import be.vinci.pae.domain.ObjectFactory;
import be.vinci.pae.domain.ObjectFactoryImpl;
import be.vinci.pae.domain.ObjectUCC;
import be.vinci.pae.domain.ObjectUCCImpl;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.UserFactoryImpl;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.domain.UserUCCImpl;
import be.vinci.pae.services.AvailableDAO;
import be.vinci.pae.services.AvailableDAOImpl;
import be.vinci.pae.services.DALBackendServices;
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
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * ApplicationBinder class.
 */
@Provider
public class ApplicationBinder extends AbstractBinder {

  /**
   * configure is used to create instance of different objets.
   */
  @Override
  protected void configure() {
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(UserDAOImpl.class).to(UserDAO.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(DALBackendServicesImpl.class).to(DALBackendServices.class).to(DALServices.class)
        .in(Singleton.class);

    bind(ObjectFactoryImpl.class).to(ObjectFactory.class).in(Singleton.class);
    bind(ObjectDAOImpl.class).to(ObjectDAO.class).in(Singleton.class);
    bind(ObjectUCCImpl.class).to(ObjectUCC.class).in(Singleton.class);

    bind(FilterFactoryImpl.class).to(FilterFactory.class).in(Singleton.class);
    bind(FilterDAOImpl.class).to(FilterDAO.class).in(Singleton.class);
    bind(FilterUCCImpl.class).to(FilterUCC.class).in(Singleton.class);

    bind(NotificationFactoryImpl.class).to(NotificationFactory.class).in(Singleton.class);
    bind(NotificationDAOImpl.class).to(NotificationDAO.class).in(Singleton.class);
    bind(NotificationUCCImpl.class).to(NotificationUCC.class).in(Singleton.class);

    bind(AvailableFactoryImpl.class).to(AvailableFactory.class).in(Singleton.class);
    bind(AvailableDAOImpl.class).to(AvailableDAO.class).in(Singleton.class);
    bind(AvailableUCCImpl.class).to(AvailableUCC.class).in(Singleton.class);


  }
}