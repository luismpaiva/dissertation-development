/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dokes.controller;

/**
 *
 * @author Luis
 */
    public class Rule {
        public int id;
        private String premise;
        private String conclusion;
        private String confidence;
        private String conviction;
        private String gain;
        private String lift;
        private String laplace;
        private String ps;
        private String totalsupport;
        
        public Rule()
        {
            initRule();
        }
        
        public void initRule()
        {
            id = 0;
            setPremise("");
            setConclusion("");
            setConfidence("");
            setConviction("");
            setGain("");
            setLift("");
            setLaplace("");
            setPs("");
            setTotalsupport("");
        }

    /**
     * @return the premise
     */
    public String getPremise() {
        return premise;
    }

    /**
     * @param premise the premise to set
     */
    public void setPremise(String premise) {
        this.premise = premise;
    }

    /**
     * @return the conclusion
     */
    public String getConclusion() {
        return conclusion;
    }

    /**
     * @param conclusion the conclusion to set
     */
    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    /**
     * @return the confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    /**
     * @return the conviction
     */
    public String getConviction() {
        return conviction;
    }

    /**
     * @param conviction the conviction to set
     */
    public void setConviction(String conviction) {
        this.conviction = conviction;
    }

    /**
     * @return the gain
     */
    public String getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(String gain) {
        this.gain = gain;
    }

    /**
     * @return the lift
     */
    public String getLift() {
        return lift;
    }

    /**
     * @param lift the lift to set
     */
    public void setLift(String lift) {
        this.lift = lift;
    }

    /**
     * @return the laplace
     */
    public String getLaplace() {
        return laplace;
    }

    /**
     * @param laplace the laplace to set
     */
    public void setLaplace(String laplace) {
        this.laplace = laplace;
    }

    /**
     * @return the ps
     */
    public String getPs() {
        return ps;
    }

    /**
     * @param ps the ps to set
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * @return the totalsupport
     */
    public String getTotalsupport() {
        return totalsupport;
    }

    /**
     * @param totalsupport the totalsupport to set
     */
    public void setTotalsupport(String totalsupport) {
        this.totalsupport = totalsupport;
    }
        
        
        
    }