package edu.cu.cs.sms.util;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of <code>javax.servlet.Filter</code> used to compress the
 * ServletResponse if it is bigger than a threshold.
 * 
 * @author Amy Roh
 * @author Dmitri Valdin
 * @author John Weatherley 
 * @version $Id$
 */

public class CompressionFilter implements Filter {

    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    private FilterConfig config = null;

    /**
     * Minimal reasonable threshold
     */
    private int minThreshold = 128;

    /**
     * The threshold number to compress
     */
    protected int compressionThreshold;

    /**
     * Debug level for this filter
     */
    private int debug = 0;

    /**
     * Place this filter into service.
     * 
     * @param filterConfig
     *            The filter configuration object
     */

    public void init(FilterConfig filterConfig) {

        config = filterConfig;
        if (filterConfig != null) {
            String value = filterConfig.getInitParameter("debug");
            if (value != null) {
                debug = Integer.parseInt(value);
            } else {
                debug = 0;
            }
            String str = filterConfig.getInitParameter("compressionThreshold");
            if (str != null) {
                compressionThreshold = Integer.parseInt(str);
                if (compressionThreshold != 0
                        && compressionThreshold < minThreshold) {
                    if (debug > 0) {
                        System.out
                                .println("compressionThreshold should be either 0 - no compression or >= "
                                        + minThreshold);
                        System.out.println("compressionThreshold set to "
                                + minThreshold);
                    }
                    compressionThreshold = minThreshold;
                }
            } else {
                compressionThreshold = 0;
            }

        } else {
            compressionThreshold = 0;
        }

    }

    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.config = null;

    }

    /**
     * The <code>doFilter</code> method of the Filter is called by the
     * container each time a request/response pair is passed through the chain
     * due to a client request for a resource at the end of the chain. The
     * FilterChain passed into this method allows the Filter to pass on the
     * request and response to the next entity in the chain.
     * <p>
     * This method first examines the request to check whether the client
     * support compression. <br>
     * It simply just pass the request and response if there is no support for
     * compression.<br>
     * If the compression support is available, it creates a
     * CompressionServletResponseWrapper object which compresses the content and
     * modifies the header if the content length is big enough. It then invokes
     * the next entity in the chain using the FilterChain object (<code>chain.doFilter()</code>),
     * <br>
     */

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
				
        if (debug > 0) {
            System.out.println("@doFilter");
        }
		
        if (compressionThreshold == 0) {
            if (debug > 0) {
                System.out
                        .println("doFilter gets called, but compressionTreshold is set to 0 - no compression");
            }
            chain.doFilter(request, response);
            return;
        }

        boolean supportCompression = false;
        if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = ((HttpServletRequest)request);
			String realPath = httpServletRequest.getRequestURI().toLowerCase();
			if (debug > 1) {
                System.out.println("requestURI = " + realPath);
            }
			
			// Exclude images: IE 6 and lower does not properly handle gziped images (can cause browser to hang).
			if(realPath.endsWith(".png") || realPath.endsWith(".jpg") || realPath.endsWith(".gif") || realPath.endsWith(".cur")){
				if (debug > 0) {
					System.out.println("image: doFilter gets called wo compression");
				}
				chain.doFilter(request, response);
				return;			
			}

            // Are we allowed to compress ?
            String s = (String) httpServletRequest.getParameter("gzip");
            if ("false".equalsIgnoreCase(s)) {
                if (debug > 0) {
                    System.out
                            .println("got parameter gzip=false --> don't compress, just chain filter");
                }
                chain.doFilter(request, response);
                return;
            }

            Enumeration e = httpServletRequest.getHeaders("Accept-Encoding");
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                if (name.indexOf("gzip") != -1) {
                    if (debug > 0) {
                        System.out.println("supports compression");
                    }
                    supportCompression = true;
                } else {
                    if (debug > 0) {
                        System.out.println("no support for compresion");
                    }
                }
            }
        }

        if (!supportCompression) {
            if (debug > 0) {
                System.out.println("doFilter gets called wo compression");
            }
            chain.doFilter(request, response);
            return;
        } else {
            if (response instanceof HttpServletResponse) {
                CompressionServletResponseWrapper wrappedResponse = new CompressionServletResponseWrapper(
                        (HttpServletResponse) response);
                wrappedResponse.setDebugLevel(debug);
                wrappedResponse.setCompressionThreshold(compressionThreshold);
                if (debug > 0) {
                    System.out.println("doFilter gets called with compression");
                }
                try {
                    chain.doFilter(request, wrappedResponse);
                } finally {
                    wrappedResponse.finishResponse();
                }
                return;
            }
        }
    }

    /**
     * Set filter config This function is equivalent to init. Required by
     * Weblogic 6.1
     * 
     * @param filterConfig
     *            The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        init(filterConfig);
    }

    /**
     * Return filter config Required by Weblogic 6.1
     */
    public FilterConfig getFilterConfig() {
        return config;
    }

}