package uk.gov.re.gds.metrics;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class AuthenticationFilterTest {

	@InjectMocks
	private AuthenticationFilter authenticationFilter;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain chain;

	@Mock
	private FilterConfig config;


	@Mock
	private Configuration configuration;

	@Test
	public void notRelevantPath() throws ServletException, IOException {
		Mockito.when(request.getRequestURI()).thenReturn("/index");
		Mockito.when(configuration.getPrometheusMetricsPath()).thenReturn("/metrics");

		this.prepareConfigurationMock();
		this.testFilter();
	}

	@Test
	public void relevantPathAndAuthDisabled() throws ServletException, IOException {
		Mockito.when(configuration.getPrometheusMetricsPath()).thenReturn("/metrics");
		Mockito.when(configuration.isAuthEnable()).thenReturn(false);

		this.prepareConfigurationMock();
		this.testFilter();

		assertThat(response.getStatus(), IsEqual.equalTo(200));
	}

	private void prepareConfigurationMock() {
		mockStatic(Configuration.class);
		when(Configuration.getInstance()).thenReturn(configuration);
	}

	private void testFilter() throws ServletException, IOException {
		authenticationFilter.init(config);
		authenticationFilter.doFilter(request, response, chain);
		authenticationFilter.destroy();
	}

	@Path("/metrics")
	public static class MetricsTestResource {

		@GET
		public Response simpleTest() {
			return Response.ok().build();
		}
	}

	@Path("/index")
	public static class IndexTestResource {
		@GET
		public Response simpleTest() {
			return Response.ok().build();
		}
	}
}