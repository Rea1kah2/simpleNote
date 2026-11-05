import XCTest
@testable import SimpleNote

final class DateHelpersTests: XCTestCase {
    
    func testStartOfDay() {
        let now = Date()
        let start = now.startOfDay
        let calendar = Calendar.current
        
        let components = calendar.dateComponents([.hour, .minute, .second], from: start)
        XCTAssertEqual(components.hour, 0)
        XCTAssertEqual(components.minute, 0)
        XCTAssertEqual(components.second, 0)
    }
    
    func testIsSameDay() {
        let now = Date()
        let start = now.startOfDay
        XCTAssertTrue(now.isSameDay(as: start))
        
        if let yesterday = Calendar.current.date(byAdding: .day, value: -1, to: now) {
            XCTAssertFalse(now.isSameDay(as: yesterday))
        }
    }
    
    func testStartOfMonth() {
        let now = Date()
        let startOfMonth = now.startOfMonth
        let calendar = Calendar.current
        let day = calendar.component(.day, from: startOfMonth)
        XCTAssertEqual(day, 1)
    }
    
    func testRelativeDayString() {
        let today = Date()
        XCTAssertEqual(today.relativeDayString, "Hari Ini")
        
        if let yesterday = Calendar.current.date(byAdding: .day, value: -1, to: today) {
            XCTAssertEqual(yesterday.relativeDayString, "Kemarin")
        }
    }
}
