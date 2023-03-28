package eu.europa.ec.eurostat.wihp.domain.enumeration;

/**
 * The AcquisitionAction enumeration.
 */
public enum AcquisitionAction {
    SUBMITTING(null),
    SUBMIT(AcquisitionStatusEnum.PROVISIONING),
    START(AcquisitionStatusEnum.STARTING),
    STARTING(null),
    PAUSE(AcquisitionStatusEnum.PAUSING),
    PAUSING(null),
    STOP(AcquisitionStatusEnum.STOPPING),
    STOPPING(null);

    private AcquisitionStatusEnum acquisitionStatus;

    AcquisitionAction(final AcquisitionStatusEnum acquisitionStatus) {
        this.acquisitionStatus = acquisitionStatus;
    }

    public AcquisitionStatusEnum getAcquisitionStatus() {
        return acquisitionStatus;
    }
}
