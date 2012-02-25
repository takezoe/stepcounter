/*
 * Created on 2003/06/19
 *
 * Ant�p�J�X�^���^�X�N
 * �R���p�C������Ƃ��̓N���X�p�X��ant.jar���w�肵�Ă��������B
 * �ł�Ant�ŃR���p�C������΁A���Ɏw�肵�Ȃ��Ă�OK
 */

package jp.sf.amateras.stepcounter.ant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.sf.amateras.stepcounter.CountResult;
import jp.sf.amateras.stepcounter.StepCounter;
import jp.sf.amateras.stepcounter.StepCounterFactory;
import jp.sf.amateras.stepcounter.Util;
import jp.sf.amateras.stepcounter.format.FormatterFactory;
import jp.sf.amateras.stepcounter.format.ResultFormatter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * �X�e�b�v�J�E���^�����s����Ant�^�X�N�ł��B
 * ����q��fileset�Afilelist�^�O�Ńt�@�C�����w�肵�܂��B
 *
 * @author sawat
 * @author hidekatsu.izuno
 */
public class StepCounterTask extends Task {
	private File output;
	private String format = "";
	private String encoding;
	private List<ResourceCollection> rcs = new ArrayList<ResourceCollection>();
	private boolean showDirectory = false;
	private boolean directoryAsCategory = false;
	private boolean defaultExcludes = true;
	private boolean failonerror = true;

	/**
	 * �o�͂���t�@�C�����w�肵�܂��B
	 *
	 * @param file �o�͂���t�@�C��
	 */
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * �t�H�[�}�b�g���w�肵�܂��B
	 *
	 * @param format �t�H�[�}�b�g
	 */
	public void setFormat(String format){
		this.format = format;
	}

	/**
	 * �\�[�X�t�@�C���̕����R�[�h���w�肵�܂��B
	 *
	 * @param encoding �����R�[�h
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * ���\�[�X�E�R���N�V������ǉ����܂��B
	 *
	 * @param res ���\�[�X�E�R���N�V����
	 */
    public void add(ResourceCollection res) {
    	rcs.add(res);
    }

	/**
	 * �f�B���N�g�����o�͂��邩�w�肵�܂��B�f�t�H���g�� false �ł��B
	 *
	 * @param showDirectory �f�B���N�g�����o�͂���ꍇ true
	 */
    public void setShowDirectory(boolean showDirectory) {
    	this.showDirectory = showDirectory;
    }

	/**
	 * �J�e�S�����Ƃ��ċN�_�f�B���N�g�����g�p���邩�w�肵�܂��B�f�t�H���g�� false �ł��B
	 *
	 * @param directoryAsCategory �J�e�S�����Ƃ��ċN�_�f�B���N�g�����g�p����ꍇ true
	 */
    public void setDirectoryAsCategory(boolean directoryAsCategory) {
    	this.directoryAsCategory = directoryAsCategory;
    }

	/**
	 * �f�t�H���g�̏��O�ݒ��L���ɂ��邩�w�肵�܂��B�f�t�H���g�� true �ł��B
	 *
	 * @param showDirectory �f�t�H���g�̏��O�ݒ��L���ɂ���ꍇ true
	 */
    public void setDefaultexcludes(boolean defaultExcludes) {
    	this.defaultExcludes = defaultExcludes;
    }

	/**
	 * �t�@�C�������݂��Ȃ��ȂǃG���[�������ɓ�����~�����邩�w�肵�܂��B
	 *
	 * @param failonerror �G���[�������ɓ�����~������ꍇ true
	 */
    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }

	/**
	 * �X�e�b�v����������s���܂��B
	 *
	 * @see org.apache.tools.ant.Task#execute()
	 */
    public void execute() throws BuildException {
    	ResultFormatter formatter = FormatterFactory.getFormatter(format);

    	if (encoding != null) Util.setFileEncoding(encoding);

    	OutputStream out = null;
    	try {
	    	if (output != null) {
	    		try {
					out = new BufferedOutputStream(new FileOutputStream(output));
				} catch (FileNotFoundException e) {
					throw new BuildException("One of tofile or todir must be set.", e);
				}
	    	} else {
	    		out = System.out;
	    	}

	    	Map<FileSet, ResourceCollection> fsList = new LinkedHashMap<FileSet, ResourceCollection>();
	    	for (ResourceCollection rc : rcs) {
	    		if (rc instanceof FileList && rc.isFilesystemOnly()) {
	    			FileList fl = (FileList)rc;

    				FileSet fs = new FileSet();
    				fs.setDir(fl.getDir(getProject()));
   					fs.appendIncludes(fl.getFiles(getProject()));
    				fsList.put(fs, rc);
	    		} else if (rc instanceof FileSet && rc.isFilesystemOnly()) {
	    			fsList.put((FileSet)rc, rc);
	    		} else {
	    			throw new BuildException("Only FileSystem resources are supported.");
	    		}
	    	}

    		List<CountResult> results = new ArrayList<CountResult>();
	    	for (Map.Entry<FileSet, ResourceCollection> entry : fsList.entrySet()) {
	    		FileSet fs = entry.getKey();
    			fs.setDefaultexcludes(defaultExcludes);

    			DirectoryScanner ds = null;
                try {
                    ds = fs.getDirectoryScanner(getProject());
                } catch (BuildException e) {
                    if (failonerror || !getMessage(e).endsWith(DirectoryScanner.DOES_NOT_EXIST_POSTFIX)) {
                        throw e;
                    } else {
                        log("Warning: " + getMessage(e), Project.MSG_ERR);
                        continue;
                    }
                }

                File baseDir = fs.getDir(getProject());
                if (!baseDir.exists()) {
                	throw new BuildException("basedir \"" + baseDir.getPath() + "\" does not exist!");
                }

                String basePath;
				try {
					basePath = baseDir.getCanonicalPath();
				} catch (IOException e) {
					throw new BuildException("I/O Error: " + baseDir, e);
				}

        		for (String name : ds.getIncludedFiles()) {
        			File file = new File(baseDir, name);
        			try {
        				CountResult result = count(file);
        				if (showDirectory) {
        					name = file.getCanonicalPath();
        					if (name.startsWith(basePath)) {
        						name = name.substring(basePath.length());
        					}
        					name = name.replace('\\', '/');
        					result.setFileName(name);
        				}
        				if (directoryAsCategory) {
        					result.setCategory(baseDir.getName());
        				}
    					results.add(result);
        			} catch (IOException e) {
                        if (failonerror) {
                        	throw new BuildException("I/O Error: " + file, e);
                        } else {
                        	log("Warning: " + getMessage(e), Project.MSG_ERR);
                        	continue;
                        }
        			}
        		}
	    	}

	    	log("" + fsList.size() + " �N�_�f�B���N�g�� / " + results.size() + " �t�@�C��");

	    	out.write(formatter.format(results.toArray(new CountResult[results.size()])));
	    	out.flush();

	    	if (output != null) {
	    		log(output.getAbsolutePath() + " �ɃJ�E���g���ʂ��o�͂��܂����B");
	    	}
    	} catch (IOException e) {
			throw new BuildException("I/O Error", e);
    	} finally {
    		try {
    			if (out != null && output != null) out.close();
			} catch (IOException e) {
				throw new BuildException("I/O Error", e);
			}
    	}
    }

    private CountResult count(File file) throws IOException {
		StepCounter counter = StepCounterFactory.getCounter(file.getName());
		if (counter != null) {
			return counter.count(file, Util.getFileEncoding(file));
		} else {
			return new CountResult(file, file.getName(), null, null, 0, 0, 0);
		}
    }

    private String getMessage(Exception ex) {
        return ex.getMessage() == null ? ex.toString() : ex.getMessage();
    }
}
