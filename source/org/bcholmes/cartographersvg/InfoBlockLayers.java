package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InfoBlockLayers extends InfoBlock {
	
	static class Layer {
		private short id;
		private short flags;
		private String name;
		private short styleId;

		static Layer from(ByteBuffer buffer) {
			Layer layer = new Layer();
			int length = buffer.getInt();
			if (length > 0) {
				layer.id = buffer.getShort();
				layer.flags = buffer.getShort();
				layer.styleId = buffer.getShort();
				
				layer.name = BaseShape.getString(buffer, length - 10);
				
				return layer;
			} else {
				return null;
			}
		}

		public String getName() {
			return this.name;
		}

		public boolean isHidden() {
			return (this.flags & 0x01) != 0;
		}

		public boolean isLocked() {
			return (this.flags & 0x02) != 0;
		}

		public short getId() {
			return this.id;
		}
		
	}

	private List<Layer> layers = new ArrayList<Layer>();

	public InfoBlockLayers(int length, int etype, int itype) {
		super(length, etype, itype);
	}

	public static InfoBlock from(int length, int etype, int itype,
			ByteBuffer buffer) {
		
		InfoBlockLayers result = new InfoBlockLayers(length, etype, itype);
		buffer.getInt();
		buffer.get();
		buffer.get();

		short creator = buffer.getShort();
		int count = buffer.getInt();
		short version = buffer.getShort();
		short nextId = buffer.getShort();
		
		while (true) {
			Layer layer = Layer.from(buffer);
			if (layer != null) {
				result.layers.add(layer);
			} else {
				break;
			}
		}
		
		return result;
	}
	
	public Layer getLayerById(short id) {
		Layer result = null;
		for (Layer layer : this.layers) {
			if (id == layer.id) {
				result = layer;
				break;
			}
		}
		return result;
	}

	public List<Layer> getLayers() {
		return this.layers;
	}

}
