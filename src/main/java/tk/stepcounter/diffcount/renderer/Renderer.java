package tk.stepcounter.diffcount.renderer;

import tk.stepcounter.diffcount.object.DiffFolderResult;

public interface Renderer {

	public byte[] render(DiffFolderResult root);

}
