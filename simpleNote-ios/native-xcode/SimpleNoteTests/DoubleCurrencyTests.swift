import XCTest
@testable import SimpleNote

final class DoubleCurrencyTests: XCTestCase {
    
    func testToIndonesianRupiahStandardAmounts() {
        let amount1: Double = 50000
        let formatted1 = amount1.toIndonesianRupiah
        XCTAssertTrue(formatted1.contains("50.000") || formatted1.contains("50,000"), "Harus memiliki separator ribuan: \(formatted1)")
        XCTAssertTrue(formatted1.contains("Rp"), "Harus diawali simbol Rp: \(formatted1)")
        
        let amount2: Double = 1250000
        let formatted2 = amount2.toIndonesianRupiah
        XCTAssertTrue(formatted2.contains("1.250.000") || formatted2.contains("1,250,000"), "Harus memformat jutaan: \(formatted2)")
    }
    
    func testToIndonesianRupiahZeroAndSmallAmounts() {
        let zero: Double = 0
        XCTAssertTrue(zero.toIndonesianRupiah.contains("0"))
        
        let small: Double = 500
        XCTAssertTrue(small.toIndonesianRupiah.contains("500"))
    }
    
    func testToCompactRupiah() {
        let thousand: Double = 50000
        let formattedThousand = thousand.toCompactRupiah
        XCTAssertTrue(formattedThousand.contains("50rb") || formattedThousand.contains("50.000"))
        
        let million: Double = 1500000
        let formattedMillion = million.toCompactRupiah
        XCTAssertTrue(formattedMillion.contains("jt") || formattedMillion.contains("1.5jt"))
    }
}
