package org.pure4j.model;

public class PackageHandle extends AbstractHandle<Package> implements Comparable<PackageHandle> {
	
	private String packageName;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackageHandle other = (PackageHandle) obj;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}

	private String className;
	
	public PackageHandle(Class<?> classForPackage) {
		this.className = AbstractHandle.convertClassName(classForPackage);
		this.packageName = AbstractHandle.convertPackageName(classForPackage.getPackage());
	}
	
	public PackageHandle(String packageName, String className) {
		this.packageName = packageName;
		this.className = className;
	}

	public Package hydrate(ClassLoader cl) {
		return AbstractHandle.hydratePackage(packageName, className, cl);		
	}

	public int compareTo(PackageHandle o) {
		return this.packageName.compareTo(o.packageName);
	}

}
