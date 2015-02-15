package com.github.florinn.restr.util;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final ClassLoaderAccessor THREAD_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
		@Override
		protected ClassLoader doGetClassLoader() throws Throwable {
			return Thread.currentThread().getContextClassLoader();
		}
	};

	private static final ClassLoaderAccessor CLASS_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
		@Override
		protected ClassLoader doGetClassLoader() throws Throwable {
			return Utils.class.getClassLoader();
		}
	};

	private static final ClassLoaderAccessor SYSTEM_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
		@Override
		protected ClassLoader doGetClassLoader() throws Throwable {
			return ClassLoader.getSystemClassLoader();
		}
	};

	public static InputStream getResourceAsStream(String name) {

		InputStream is = THREAD_CL_ACCESSOR.getResourceStream(name);

		if (is == null) {
			log.trace("Resource [{}] was not found via the thread context ClassLoader. Trying the current ClassLoader...", name);
			is = CLASS_CL_ACCESSOR.getResourceStream(name);
		}

		if (is == null) {
			log.trace("Resource [{}] was not found via the current class loader. Trying the system/application ClassLoader...", name);
			is = SYSTEM_CL_ACCESSOR.getResourceStream(name);
		}

		if (is == null) {
			log.trace("Resource [{}] was not found via the thread context, current, or " +
					"system/application ClassLoaders. All heuristics have been exhausted. Returning null.", name);
		}

		return is;
	}

	private static interface ClassLoaderAccessor {
		Class<?> loadClass(String fqcn);

		InputStream getResourceStream(String name);
	}

	private static abstract class ExceptionIgnoringAccessor implements ClassLoaderAccessor {

		public Class<?> loadClass(String fqcn) {
			Class<?> clazz = null;
			ClassLoader cl = getClassLoader();
			if (cl != null) {
				try {
					clazz = cl.loadClass(fqcn);
				} catch (ClassNotFoundException e) {
					log.trace("Unable to load clazz named [{}] from class loader [{}]", fqcn, cl);
				}
			}
			return clazz;
		}

		public InputStream getResourceStream(String name) {
			InputStream is = null;
			ClassLoader cl = getClassLoader();
			if (cl != null) {
				is = cl.getResourceAsStream(name);
			}
			return is;
		}

		protected final ClassLoader getClassLoader() {
			try {
				return doGetClassLoader();
			} catch (Throwable t) {
				log.debug("Unable to acquire ClassLoader.", t);
			}
			return null;
		}

		protected abstract ClassLoader doGetClassLoader() throws Throwable;
	}

	public static String clean(String in) {
		String out = in;
		if (in != null) {
			out = in.trim();
			if (out.equals(""))	
				out = null;
		}
		return out;
	}

}
