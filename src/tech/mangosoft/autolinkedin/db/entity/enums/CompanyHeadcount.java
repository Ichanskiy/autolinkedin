package tech.mangosoft.autolinkedin.db.entity.enums;

public enum CompanyHeadcount {
    ONE_TEN(0),                     // 1-10
    ELEVEN_FIFTY(1),                // 11-50
    FIFTYONE_TWOHUNDRED(2),         // 51-200
    TWOHUNDREDONE_FIVEHUNDRED(3),   // 201-500
    FIVEHUNDREDONE_ONETHOUSAND(4),  //501-1000
    ONETHOUSANDONE_FIVETHOUSAND(5), //1001-5000
    FIVETHOUSANDONE_TENHOUSAND(6),  //5001-10,000
    TENHOUSAND_PLUS(7);              //10,000+

    private int id;

    CompanyHeadcount(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CompanyHeadcount getCompanyHeadcountById(int id) throws Exception {
        for (CompanyHeadcount e : values()) {
            if (e.id == id) return e;
        }
        throw new Exception("No CompanyHeadcount defined with id " + id);
    }
}
