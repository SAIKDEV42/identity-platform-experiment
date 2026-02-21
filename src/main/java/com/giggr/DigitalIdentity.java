//package com.giggr.identity.domain.model;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.util.Objects;
//
//public class DigitalIdentity {
//
//    private  final String digitalId;
//    private IdentityState state;
//    private final LocalDate dateOfBirth;
//
//
//    public DigitalIdentity(String digitalId, LocalDate dateOfBirth) {
//
//
//        if (digitalId==null||digitalId.length()!=16||!digitalId.matches("\\d{16}")){
//            throw new IllegalArgumentException("Digital ID must be exactly 16 digits");
//        }
//
//        if (dateOfBirth == null) {
//            throw new IllegalArgumentException("Date of birth is required");
//        }
//
//        int age = Period.between(dateOfBirth,LocalDate.now()).getYears();
//
//        if (age<8){
//            throw new IllegalArgumentException("Minimum age is 8");
//        }
//
//
//        this.digitalId = digitalId;
//        this.dateOfBirth = dateOfBirth;
//        this.state=IdentityState.UNVERIFIED;
//    }
//
//    void verify(){
//        if(state!=IdentityState.UNVERIFIED){
//            throw new IllegalStateException("Identity can only be verified once");
//        }
//        this.state=IdentityState.VERIFIED;
//    }
//
//
//    void activate(boolean parentalConsentProvided){
//
//        if(state==IdentityState.ACTIVE){
//            throw new IllegalStateException("Identity can only be actived once");
//        }
//        if (state!=IdentityState.VERIFIED){
//            throw new IllegalStateException("Identity must be verified before activation");
//        }
//        if (isMinor() && !parentalConsentProvided) {
//            throw new IllegalStateException("Parental consent required for minors");
//        }
//
//        this.state = IdentityState.ACTIVE;
//
//    }
//
//    private boolean isMinor() {
//        return  Period.between(dateOfBirth, LocalDate.now()).getYears() < 18;
//    }
//
//
//
//
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof DigitalIdentity)) return false;
//        DigitalIdentity foreignorId = (DigitalIdentity) o;
//        return digitalId.equals(foreignorId.digitalId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(digitalId);
//    }
//
//    public String getDigitalId() {
//        return digitalId;
//    }
//
//    public IdentityState getState() {
//        return state;
//    }
//
//    public LocalDate getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//
//
//}
