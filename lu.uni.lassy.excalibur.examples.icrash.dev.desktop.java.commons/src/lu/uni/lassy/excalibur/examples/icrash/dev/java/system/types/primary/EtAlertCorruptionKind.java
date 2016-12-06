package lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.primary;

import lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.design.JIntIs;
import lu.uni.lassy.excalibur.examples.icrash.dev.java.types.stdlib.PtBoolean;

/**
 * The Enum EtAlertCorruptionKind, which dictates the type of the alert corruption.
 */
public enum EtAlertCorruptionKind  implements JIntIs{
	
	/** A wasted alert (in main and buckup db). */
	corrupted,
	/** A restored from backup db alert. */
	restored,
	/** Uncorrupetd alert. */
	regular;
	
	/* (non-Javadoc)
	 * @see lu.uni.lassy.excalibur.examples.icrash.dev.java.system.types.design.DtIs#is()
	 */
	public PtBoolean is(){
		return new PtBoolean(this.name() == EtAlertCorruptionKind.corrupted.name() ||
				this.name() == EtAlertCorruptionKind.regular.name()|| this.name() == EtAlertCorruptionKind.restored.name());
	}
}
