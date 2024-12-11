import React, { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { UserContext } from "../Common/UserContext";

/**
 * SimLog component for displaying WebSocket messages.
 * Connects to a WebSocket server to receive and display real-time messages.
 */
const SimLog: React.FC = () => {
    // Extract the eventId parameter from the URL
    const { eventId } = useParams<{ eventId: string }>();
    // Get the user data from the UserContext
    const { userData } = useContext(UserContext) || {};
    // State to store the received WebSocket messages
    const [messages, setMessages] = useState<string[]>([]);

    /**
     * Establishes a WebSocket connection to receive real-time messages.
     * Closes the WebSocket connection when the component is unmounted.
     */
    useEffect(() => {
        if (userData) {
            // const socket = new WebSocket(`ws://localhost:8080/ws/text`);
            const socket = new WebSocket(
                `wss://ticketing---system-32a1f2f59169.herokuapp.com/ws/text`
            );

            socket.onopen = () =>
                console.log("WebSocket connection established");

            socket.onmessage = (event) =>
                setMessages((prev) => [...prev, event.data]);

            socket.onerror = (error) =>
                console.error("WebSocket error:", error);

            socket.onclose = () => console.log("WebSocket connection closed");

            return () => socket.close();
        }
    }, [eventId, userData]);

    return (
        <div>
            <h2>WebSocket Messages</h2>
            <ul style={{ textAlign: "left" }}>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
            </ul>
        </div>
    );
};

export default SimLog;