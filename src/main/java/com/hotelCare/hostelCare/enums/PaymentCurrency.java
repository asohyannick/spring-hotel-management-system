package com.hotelCare.hostelCare.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentCurrency {

    // --- Major global ---
    USD("United States Dollar"),
    EUR("Euro"),
    GBP("British Pound Sterling"),
    JPY("Japanese Yen"),
    CHF("Swiss Franc"),
    CAD("Canadian Dollar"),
    AUD("Australian Dollar"),
    NZD("New Zealand Dollar"),

    // --- Africa ---
    NGN("Nigerian Naira"),
    GHS("Ghanaian Cedi"),
    ZAR("South African Rand"),
    KES("Kenyan Shilling"),
    UGX("Ugandan Shilling"),
    TZS("Tanzanian Shilling"),
    XOF("West African CFA Franc"),
    XAF("Central African CFA Franc"),
    MAD("Moroccan Dirham"),
    EGP("Egyptian Pound"),

    // --- Americas ---
    MXN("Mexican Peso"),
    BRL("Brazilian Real"),
    ARS("Argentine Peso"),
    CLP("Chilean Peso"),
    COP("Colombian Peso"),
    PEN("Peruvian Sol"),
    UYU("Uruguayan Peso"),

    // --- Europe ---
    SEK("Swedish Krona"),
    NOK("Norwegian Krone"),
    DKK("Danish Krone"),
    PLN("Polish Zloty"),
    CZK("Czech Koruna"),
    HUF("Hungarian Forint"),
    RON("Romanian Leu"),

    // --- Middle East ---
    AED("UAE Dirham"),
    SAR("Saudi Riyal"),
    QAR("Qatari Riyal"),
    KWD("Kuwaiti Dinar"),
    BHD("Bahraini Dinar"),
    OMR("Omani Rial"),
    ILS("Israeli New Shekel"),
    TRY("Turkish Lira"),

    // --- Asia ---
    CNY("Chinese Yuan"),
    INR("Indian Rupee"),
    PKR("Pakistani Rupee"),
    BDT("Bangladeshi Taka"),
    LKR("Sri Lankan Rupee"),
    THB("Thai Baht"),
    VND("Vietnamese Dong"),
    MYR("Malaysian Ringgit"),
    SGD("Singapore Dollar"),
    IDR("Indonesian Rupiah"),
    PHP("Philippine Peso"),
    KRW("South Korean Won"),
    HKD("Hong Kong Dollar"),

    // --- Oceania ---
    FJD("Fijian Dollar"),
    PGK("Papua New Guinean Kina"),
    WST("Samoan Tala"),

    // --- Special / ISO ---
    XAU("Gold"),
    XAG("Silver");

    private final String description;

    /** Serialize enum as "USD", "EUR", etc. */
    @JsonValue
    public String getCode() {
        return name();
    }

    /** Deserialize JSON -> enum safely */
    @JsonCreator
    public static PaymentCurrency fromValue(String value) {
        return PaymentCurrency.valueOf(value.toUpperCase());
    }
}

