import React, { useEffect, useState, useContext } from "react";
import { UserContext } from "../Common/UserContext";
import API from "../../axios.tsx";
import Box from "@mui/material/Box";
import LinearProgress from "@mui/material/LinearProgress";
import Chip from "@mui/material/Chip";

interface SimDataProps {
    eventId: string;
}

interface Event {
    eventId: string;
    name: string;
    totalTickets: number;
    maxCapacity: number;
}

/**
 * SimData component for displaying simulation data of an event.
 * Fetches event details and listens to WebSocket for real-time ticket updates.
 * @param eventId - The ID of the event to fetch and display data for.
 */
const SimData: React.FC<SimDataProps> = ({ eventId }) => {
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // State to store the event details
    const [event, setEvent] = useState<Event | null>(null);
    // State to store the current tickets data
    const [currentTickets, setCurrentTickets] = useState<number[]>([]);
    // State to store all sold tickets data
    const [allSoldTickets, setAllSoldTickets] = useState<number[]>([]);

    /**
     * Fetches event details for the current event ID.
     */
    useEffect(() => {
        const fetchEvent = async () => {
            if (userData) {
                try {
                    const response = await API.get(`/event/${eventId}`);
                    setEvent(response.data);
                } catch (error) {
                    console.error("Error fetching event:", error);
                }
            }
        };
        fetchEvent();
    }, [eventId, userData]);

    /**
     * Establishes a WebSocket connection to receive real-time ticket updates.
     */
    useEffect(() => {
        if (userData) {
            // const socket = new WebSocket(`ws://localhost:8080/ws/integers`);
            const socket = new WebSocket(
                `wss://ticketing---system-32a1f2f59169.herokuapp.com/ws/integers`
            );

            socket.onopen = () =>
                console.log("WebSocket connection established");

            socket.onmessage = (event) => {
                const parts = event.data.split(",").map(Number);
                const half = Math.ceil(parts.length / 2);
                setCurrentTickets(parts.slice(0, half));
                setAllSoldTickets(parts.slice(half));
            };

            socket.onerror = (error) =>
                console.error("WebSocket error:", error);
            socket.onclose = () => console.log("WebSocket connection closed");

            return () => socket.close();
        }
    }, [userData]);

    /**
     * Saves logs when all tickets are sold.
     */
    useEffect(() => {
        if (
            allSoldTickets.reduce((acc, ticket) => acc + ticket, 0) ===
            event?.totalTickets
        ) {
            const saveLogs = async () => {
                try {
                    await API.post(`/event/${eventId}/saveLogs`);
                    console.log("Logs saved successfully");
                } catch (error) {
                    console.error("Error saving logs:", error);
                }
            };
            saveLogs();
        }
    }, [allSoldTickets, eventId, event]);

    if (!event) return <p>Loading...</p>;

    const allAddedTickets =
        currentTickets.reduce((acc, ticket) => acc + ticket, 0) +
        allSoldTickets.reduce((acc, ticket) => acc + ticket, 0);
    const progress =
        (allSoldTickets.reduce((acc, ticket) => acc + ticket, 0) /
            event.totalTickets) *
        100;
    const buffer = (allAddedTickets / event.totalTickets) * 100;

    /**
     * Returns properties for the status chip based on the ticket sales.
     */
    const getChipProps = () => {
        const totalSold = allSoldTickets.reduce(
            (acc, ticket) => acc + ticket,
            0
        );
        if (totalSold === 0)
            return { label: "Not Started", color: "warning" as const };
        if (totalSold === event.totalTickets)
            return { label: "Finished", color: "primary" as const };
        return { label: "Running", color: "success" as const };
    };

    return (
        <div>
            <h2>Simulation Data</h2>
            <Chip {...getChipProps()} variant="outlined" />
            <h3>Static Data</h3>
            <div>Event Name - {event.name}</div>
            <div>Total Tickets - {event.totalTickets}</div>
            <div>Max Capacity - {event.maxCapacity}</div>
            <h3>Dynamic Data</h3>
            <div>
                Current Ticket Count -{" "}
                {currentTickets.length === 0 ? "Loading..." : currentTickets}
            </div>
            <div>
                All Sold Ticket Count -{" "}
                {allSoldTickets.length === 0 ? "Loading..." : allSoldTickets}
            </div>
            <div>All Added Ticket Count - {allAddedTickets}</div>
            <h2>Progress</h2>
            <Box sx={{ width: "100%" }}>
                <LinearProgress
                    variant="buffer"
                    value={progress}
                    valueBuffer={buffer}
                />
            </Box>
        </div>
    );
};

export default SimData;