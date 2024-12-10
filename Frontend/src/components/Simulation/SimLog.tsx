import React, { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { UserContext } from "../Common/UserContext";

const SimLog: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const { userData } = useContext(UserContext) || {};
    const [messages, setMessages] = useState<string[]>([]);

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
