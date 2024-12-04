import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";

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
    vendors: string[];
}

interface Vendor {
    userId: string;
    username: string;
}

const Event: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [event, setEvent] = useState<Event | null>(null);
    const [vendorDetails, setVendorDetails] = useState<{
        [key: string]: Vendor;
    }>({});

    const fetchVendorDetails = async (vendorId: string) => {
        try {
            const response = await API.get(`/vendor/${vendorId}`, {
                headers: {
                    "Content-Type": "application/json",
                },
            });
            setVendorDetails((prevDetails) => ({
                ...prevDetails,
                [vendorId]: response.data,
            }));
        } catch (error) {
            console.error(`Error fetching vendor ${vendorId}:`, error);
        }
    };

    useEffect(() => {
        const fetchEvent = async () => {
            if (userContext?.userData) {
                const url = `/event/${eventId}`;
                try {
                    const response = await API.get(url, {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
                    setEvent(response.data);
                } catch (error) {
                    console.error("Error fetching event:", error);
                }
            }
        };

        fetchEvent();
    }, [eventId, userContext]);

    useEffect(() => {
        if (event) {
            event.vendors.forEach((vendorId) => {
                if (!vendorDetails[vendorId]) {
                    fetchVendorDetails(vendorId);
                }
            });
        }
    }, [event, vendorDetails]);

    if (!event) {
        return <p>Loading...</p>;
    }

    const handleSimulateEvent = () => {
        navigate(`/simulation/${eventId}`);
    };

    const handleBuyTickets = () => {
        navigate(`/changeTickets/${eventId}`);
    };

    const handleAddTickets = () => {
        navigate(`/changeTickets/${eventId}`);
    };

    const handleAddVendors = () => {
        navigate(`/addVendors/${eventId}`);
    };

    return (
        <div>
            <h1>{event.name}</h1>
            <p>{event.desc}</p>
            {userContext?.userData?.isVendor && (
                <>
                    <p>
                        Owner:{" "}
                        {vendorDetails[event.ownerId]?.username || "Loading..."}
                    </p>
                </>
            )}
            <p>Total Tickets: {event.totalTickets}</p>
            <p>Max Capacity: {event.maxCapacity}</p>
            <p>Current Tickets: {event.currentTickets}</p>
            <p>Issued Tickets: {event.issuedTickets}</p>
            <p>Total Tickets Added: {event.totalTicketsAdded}</p>
            {userContext?.userData?.userId === event.ownerId && (
                <>
                    <h3>Vendors</h3>
                    <ul>
                        {event.vendors.map((vendorId) => (
                            <li key={vendorId}>
                                {vendorDetails[vendorId]?.username ||
                                    "Loading..."}
                            </li>
                        ))}
                    </ul>
                </>
            )}
            <div>
                {userContext?.userData?.isVendor && (
                    <>
                        <button onClick={handleSimulateEvent}>
                            Simulate Event
                        </button>
                        <button onClick={handleAddTickets}>Add Tickets</button>
                        {userContext.userData.userId === event.ownerId && (
                            <button onClick={handleAddVendors}>
                                Change Vendors
                            </button>
                        )}
                    </>
                )}
                {!userContext?.userData?.isVendor && (
                    <button onClick={handleBuyTickets}>Buy Tickets</button>
                )}
            </div>
        </div>
    );
};

export default Event;
