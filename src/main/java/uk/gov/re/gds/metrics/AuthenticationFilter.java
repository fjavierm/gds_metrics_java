package uk.gov.re.gds.metrics;

import javax.annotation.Priority;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
@Priority(500)
public class AuthenticationFilter implements Filter {

	private Configuration configuration;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		configuration = Configuration.getInstance();
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse;

		if (this.needAuthenticationCheck(httpRequest, configuration)) {
			if (!this.isAllowed(httpRequest, configuration)) {
				httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(401);
				httpResponse.getWriter().print("Request not allowed");

				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	private boolean needAuthenticationCheck(final HttpServletRequest request, final Configuration configuration) {
		return configuration.getPrometheusMetricsPath().equals(request.getRequestURI()) && configuration.isAuthEnable();
	}

	private boolean isAllowed(final HttpServletRequest request, final Configuration configuration) {
		final String httpAuthorization = request.getHeader("HTTP_AUTHORIZATION");
		final Pattern pattern = Pattern.compile("Bearer (.*)", Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattern.matcher(httpAuthorization);

		return matcher.find() && configuration.getApplicationId().equals(matcher.group(1));
	}
}
