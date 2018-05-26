package jp.sf.amateras.stepcounter.diffcount.renderer;

import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;

public interface Renderer {

	public byte[] render(DiffFolderResult root);

}
