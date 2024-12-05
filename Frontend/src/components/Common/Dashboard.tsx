import React, { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
import "./Common.css";
import Button from "@mui/material/Button";

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

const Dashboard: React.FC = () => {
    const userContext = useContext(UserContext);
    const [events, setEvents] = useState<Event[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchEvents = async () => {
            if (userContext && userContext.userData) {
                const url = userContext.userData.isVendor
                    ? "/events/vendor/" + userContext.userData.userId
                    : "/events";
                try {
                    const response = await API.get(url, {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
                    setEvents(response.data);
                } catch (error) {
                    console.error("Error fetching events:", error);
                }
            }
        };

        fetchEvents();
    }, [userContext]);

    const handleEventClick = (eventId: string) => {
        navigate(`/event/${eventId}`);
    };

    const handleCreateEventClick = () => {
        navigate("/createEvent");
    };

    const handleLogout = () => {
        navigate("/");
        window.location.reload();
    };

    return (
        <div>
            <h1>Dashboard</h1>
            {userContext && userContext.userData ? (
                <div>
                    <h3 className={"user-details"}>
                        Welcome, {userContext.userData.username}
                    </h3>
                    <p className={"user-details"}>
                        Id: {userContext.userData.userId}
                    </p>
                    <p className={"user-details"}>
                        Role:{" "}
                        {userContext.userData.isVendor ? "Vendor" : "Customer"}
                    </p>
                    <div className={"user-details"}>
                        <Button variant="outlined" onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>
                    <div className={"user-details"}>
                        <Button
                            variant="contained"
                            onClick={handleCreateEventClick}
                        >
                            Create an Event
                        </Button>
                    </div>

                    <h2>Events</h2>
                    {events.length > 0 ? (
                        <div className={"events-grid"}>
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
        </div>
    );
};

export default Dashboard;
