package be.vinci.pae.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.FilterDAO;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

/**
 * FilterUCCImplTest class.
 */
@TestInstance(Lifecycle.PER_CLASS)
class FilterUCCImplTest extends TestBinder {

  private FilterFactory filterFactory;
  private FilterUCC filterUCC;
  private FilterDAO filterDAO;
  private Filter filter1;


  /**
   * Set up the tests.
   */
  @BeforeAll
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.filterFactory = locator.getService(FilterFactory.class);
    this.filterUCC = locator.getService(FilterUCC.class);
    this.filterDAO = locator.getService(FilterDAO.class);
  }

  /**
   * Initialize the tests.
   */
  @BeforeEach
  void init() {
    Mockito.reset(filterDAO);
    filter1 = (Filter) filterFactory.getFilter();
    filter1.setId(0);
    filter1.setName("test");
  }

  /**
   * Test successful getAllFilters.
   */
  @Test
  @DisplayName("Test successful getAllFilters")
  public void successGetAllFilters() {
    List<FilterDTO> filters = new ArrayList<>();
    filters.add(filter1);

    Mockito.when(filterDAO.getAllFilters()).thenReturn(filters);

    assertEquals(filters, filterUCC.getAllFilters());
  }


  /**
   * Test GetAllFilters failed with wrong email.
   */
  @Test
  @DisplayName("Test GetAllFilters failed because list is null")
  public void failedGetAllFilters() {
    List<FilterDTO> filters = new ArrayList<>();
    filters.add(filter1);

    Mockito.when(filterDAO.getAllFilters()).thenReturn(null);

    assertThrows(BizException.class, () -> {
      filterUCC.getAllFilters();
    });
  }


}