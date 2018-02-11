package uk.gov.re.gds.metrics;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

//TODO: Check if Dropwizard has already one
@Provider
public class GZIPWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		final OutputStream outputStream = context.getOutputStream();
		final Configuration configuration = Configuration.getInstance();

		if (this.needCompression(context, configuration)) {
			context.getHeaders().putSingle("Content-Encoding", "gzip");
			context.setOutputStream(new GZIPOutputStream(outputStream));
			context.proceed();
		}
	}

	private boolean needCompression(final WriterInterceptorContext requestContext, final Configuration configuration) {
		//final String path = requestContext.getUriInfo().getPath();

		//return configuration.getPrometheusMetricsPath().equals(path) && configuration.isAuthEnable();

		return false;
	}
}
