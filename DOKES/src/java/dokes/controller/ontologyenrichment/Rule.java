package dokes.controller.ontologyenrichment;

//<editor-fold defaultstate="collapsed" desc="Imports">

import dokes.controller.ontologyenrichment.Metrics;

//</editor-fold>  

/**
 *
 * @author Luis Paiva
 */
public class Rule {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    public int id;
    
    private FrequentItem premise;
    private FrequentItem conclusion;
    private Metrics metrics;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public Rule()
    {
        this.premise = new FrequentItem("");
        this.conclusion = new FrequentItem("");
        this.metrics = new Metrics();
        initRule();
    }
    

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
    private void initRule() {
        id = 0;
        setPremise(premise);
        setConclusion(conclusion);
        setConfidence("");
        setConviction("");
        setGain("");
        setLift("");
        setLaplace("");
        setPs("");
        setTotalsupport("");
    }
    
    public boolean hasPremiseConceptsRelated() {
        return hasConceptsRelated(premise);
    }
    
    public boolean hasConclusionConceptsRelated() {
        return hasConceptsRelated(conclusion);
    }
    
    private boolean hasConceptsRelated(FrequentItem frequentItem) {
        return frequentItem.hasKeywords();
    }
    
    

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Properties">

    /**
     * @return the premise
     */
    public FrequentItem getPremise() {
        return premise;
    }

    /**
     * @param premise the premise to set
     */
    public void setPremise(FrequentItem premise) {
        this.premise = premise;
    }

    /**
     * @return the conclusion
     */
    public FrequentItem getConclusion() {
        return conclusion;
    }

    /**
     * @param conclusion the conclusion to set
     */
    public void setConclusion(FrequentItem conclusion) {
        this.conclusion = conclusion;
    }

    /**
     * @return the confidence
     */
    public String getConfidence() {
        return metrics.getConfidence();
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(String confidence) {
        metrics.setConfidence(confidence);
    }

    /**
     * @return the conviction
     */
    public String getConviction() {
        return metrics.getConviction();
    }

    /**
     * @param conviction the conviction to set
     */
    public void setConviction(String conviction) {
        metrics.setConviction(conviction);
    }

    /**
     * @return the gain
     */
    public String getGain() {
        return metrics.getGain();
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(String gain) {
        metrics.setGain(gain);
    }

    /**
     * @return the lift
     */
    public String getLift() {
        return metrics.getLift();
    }

    /**
     * @param lift the lift to set
     */
    public void setLift(String lift) {
        metrics.setLift(lift);
    }

    /**
     * @return the laplace
     */
    public String getLaplace() {
        return metrics.getLaplace();
    }

    /**
     * @param laplace the laplace to set
     */
    public void setLaplace(String laplace) {
        metrics.setLaplace(laplace);
    }

    /**
     * @return the ps
     */
    public String getPs() {
        return metrics.getPs();
    }

    /**
     * @param ps the ps to set
     */
    public void setPs(String ps) {
        metrics.setPs(ps);
    }

    /**
     * @return the totalsupport
     */
    public String getTotalsupport() {
        return metrics.getTotalsupport();
    }

    /**
     * @param totalsupport the totalsupport to set
     */
    public void setTotalsupport(String totalsupport) {
        metrics.setTotalsupport(totalsupport);
    }
    
    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }
    

    // </editor-fold>

}