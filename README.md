
BusWay is a desktop-based bus ticket booking system built using **Java Swing and MySQL.  
It provides a smooth booking experience for users and a powerful management panel for administrators.

Built With

- Java Swing — custom-painted components (no third-party UI libs)
- SwingWorker — all DB calls run off the EDT to keep UI smooth
- MySQL — relational data store for users, buses, bookings, passengers
- Custom UITheme — centralised design system with colours, fonts, buttons, cards, and table styles


Application Flow
LoginFrame
│
├── New User → RegisterFrame → LoginFrame
│
└── Logged In
│
├── Admin → Dashboard (Admin Panel)
│ ├── Manage Buses
│ ├── All Bookings
│ ├── Manage Users
│ └── Revenue Report
│
└── User → Dashboard (My Dashboard)
│
├── Book Ticket
│ └── BusSearchFrame
│ └── SeatSelectionFrame
│ └── PassengerDetailsFrame
│ └── PaymentFrame
│ └── TicketFrame
│
├── My Bookings / Cancel Ticket
│ └── ViewCancelBookingFrame
│
└── My Profile
└── ProfileFrame


Author
Rokhiya Begum 
B.Tech Student  
BusWay Project
