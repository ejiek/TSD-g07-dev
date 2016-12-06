package lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.design.JIntIs;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.DtString;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtString;

/**
 * The Class DtCRC, which holds the data type of the crc.
 */
public class DtCRC extends DtString implements JIntIs {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 227L;
	
	/**
	 * Instantiates a new datatype of the crc.
	 *
	 * @param s The primitive type of string to put into the datatype
	 */
	public DtCRC(PtString s) {
		super(s);
	}
	
	public boolean equals(DtCRC rightCRC){
		if (this.toString().equals(rightCRC.toString())) return true;
		return false;
	}
	
	private int _maxLength = 80;
	
	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.design.DtIs#is()
	 */
	public PtBoolean is(){
		return new PtBoolean((this.value.getValue().length() <= _maxLength));
	}
}
