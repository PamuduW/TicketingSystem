import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import SimInputForm from "./SimInputForm.tsx";
import SimLog from "./SimLog.tsx";

const Simulation: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const navigate = useNavigate();

    if (!eventId) {
        return <p>Error: Event ID is missing</p>;
    }

    const handleBackToEvent = () => {
        navigate(`/event/${eventId}`);
    };

    return (
        <>
            <button onClick={handleBackToEvent}>Back to Event</button>
            <SimInputForm eventId={eventId} />
            <SimLog />
        </>
    );
};

export default Simulation;
