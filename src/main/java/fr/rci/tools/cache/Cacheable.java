package fr.rci.tools.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Cacheable implements Serializable {
	
	private static final long serialVersionUID = 5670468879594320306L;
	
	private long size ;
	private Serializable value ;
	private Map<Integer, Serializable> extra = new HashMap<>();

	public Cacheable(long size, Serializable value) {
		super();
		this.size = size;
		this.value = value;
	}
	
}