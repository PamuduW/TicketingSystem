import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import "./Common.css";
import { Button, Drawer } from "@mui/material";
import CustomerInventory from "./CustomerInventory";

interface Event {
    eventId: string;
    name: string;
    ownerId: string;
    desc: string;
    totalTickets: number;
    maxCapacity: number;
    currentTickets: number;
    issuedTickets: number;
    totalTicketsAdded: number;
    tickets: object[];
    vendors: string[];
}

/**
 * Dashboard component for displaying user-specific information and events.
 * Allows vendors to create events and customers to view their inventory.
 */
const Dashboard: React.FC = () => {
    // Get user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // State to store the list of events
    const [events, setEvents] = useState<Event[]>([]);
    // State to manage the drawer open/close status
    const [drawerOpen, setDrawerOpen] = useState(false);
    // Hook to navigate to different routes
    const navigate = useNavigate();

    /**
     * Fetches events data when the component mounts or updates.
     */
    useEffect(() => {
        const fetchEvents = async () => {
            if (userData) {
                const url = userData.isVendor
                    ? `/events/vendor/${userData.userId}`
                    : "/events";
                try {
                    const { data } = await API.get(url, {
                        headers: { "Content-Type": "application/json" },
                    });
                    setEvents(data);
                } catch (error) {
                    console.error("Error fetching events:", error);
                }
            }
        };
        fetchEvents();
    }, [userData]);

    /**
     * Handles the event card click to navigate to the event details page.
     * @param eventId - The ID of the event to navigate to.
     */
    const handleEventClick = (eventId: string) => navigate(`/event/${eventId}`);

    /**
     * Handles the click to navigate to the create event page.
     */
    const handleCreateEventClick = () => navigate("/createEvent");

    /**
     * Handles the logout action.
     */
    const handleLogout = () => {
        navigate("/");
        window.location.reload();
    };

    /**
     * Opens the drawer to view customer inventory.
     */
    const handleOpenDrawer = () => {
        setDrawerOpen(true);
    };

    /**
     * Closes the drawer.
     */
    const handleCloseDrawer = () => {
        setDrawerOpen(false);
    };

    return (
        <div>
            <h1>Dashboard</h1>
            {userData ? (
                <div>
                    <h3 className="user-details">
                        Welcome, {userData.username}
                    </h3>
                    <p className="user-details">Id: {userData.userId}</p>
                    <p className="user-details">
                        Role: {userData.isVendor ? "Vendor" : "Customer"}
                    </p>
                    <div className="user-details">
                        <Button variant="text" onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>
                    {userData.isVendor && (
                        <div className="user-details">
                            <Button
                                variant="outlined"
                                onClick={handleCreateEventClick}
                            >
                                Create an Event
                            </Button>
                        </div>
                    )}
                    {!userData.isVendor && (
                        <div className="user-details">
                            <Button
                                variant="outlined"
                                onClick={handleOpenDrawer}
                            >
                                View Customer Inventory
                            </Button>
                        </div>
                    )}
                    <h2>Events</h2>
                    {events.length > 0 ? (
                        <div className="events-grid">
                            {events.map((event) => (
                                <div
                                    key={event.eventId}
                                    className="event-card"
                                    onClick={() =>
                                        handleEventClick(event.eventId)
                                    }
                                >
                                    <h4>{event.name}</h4>
                                    <p>{event.desc}</p>
                                    <p>
                                        Total Ticket Limit: {event.totalTickets}
                                    </p>
                                    <p>Max Capacity: {event.maxCapacity}</p>
                                    <p>
                                        Current Tickets: {event.currentTickets}
                                    </p>
                                    <p>Issued Tickets: {event.issuedTickets}</p>
                                    <p>
                                        Total Tickets Added:{" "}
                                        {event.totalTicketsAdded}
                                    </p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p>No events available.</p>
                    )}
                </div>
            ) : (
                <p>Loading...</p>
            )}
            <Drawer
                anchor="right"
                open={drawerOpen}
                onClose={handleCloseDrawer}
                sx={{ "& .MuiDrawer-paper": { backgroundColor: "#d4d4d4" } }}
            >
                <div style={{ width: 300, padding: 20 }}>
                    <CustomerInventory />
                </div>
            </Drawer>
        </div>
    );
};

export default Dashboard;
