import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "./UserContext";
import API from "../../axios";
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

    const handleBackToDashboard = () => {
        navigate("/dashboard");
    };

    const handleUpdateEvent = () => {
        navigate(`/updateEvent/${eventId}`);
    };

    return (
        <div>
            <div className={"user-details"}>
                <Button onClick={handleBackToDashboard}>
                    Back to Dashboard
                </Button>
            </div>
            <h1>{event.name}</h1>

            <p className={"user-details"}>
                Event Description&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                {event.desc}
            </p>
            {userContext?.userData?.isVendor && (
                <>
                    <p className={"user-details"}>
                        Owner&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                        {vendorDetails[event.ownerId]?.username || "Loading..."}
                    </p>
                </>
            )}
            <p className={"user-details"}>
                Total
                Tickets&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                {event.totalTickets}
            </p>
            <p className={"user-details"}>
                Max
                Capacity&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                {event.maxCapacity}
            </p>
            <p className={"user-details"}>
                Current
                Tickets&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                {event.currentTickets}
            </p>
            <p className={"user-details"}>
                Issued
                Tickets&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;:{" "}
                {event.issuedTickets}
            </p>
            <p className={"user-details"}>
                Total Tickets Added&#160;&#160;: {event.totalTicketsAdded}
            </p>
            {userContext?.userData?.userId === event.ownerId && (
                <>
                    <h3 className={"user-details"} style={{ paddingTop: 16 }}>
                        Vendors
                    </h3>
                    <ul style={{ marginTop: 1 }}>
                        {event.vendors.map((vendorId) => (
                            <li className={"user-details"} key={vendorId}>
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
                        <div>
                            <Button
                                variant="outlined"
                                onClick={handleAddTickets}
                            >
                                Add Tickets
                            </Button>
                        </div>
                        <div>
                            <Button
                                variant="outlined"
                                onClick={handleSimulateEvent}
                            >
                                Simulate Event
                            </Button>
                        </div>
                        <div>
                            {userContext.userData.userId === event.ownerId && (
                                <>
                                    <Button
                                        variant="outlined"
                                        onClick={handleAddVendors}
                                    >
                                        Change Vendors
                                    </Button>
                                    <Button
                                        variant="outlined"
                                        onClick={handleUpdateEvent}
                                    >
                                        Update Event
                                    </Button>
                                </>
                            )}
                        </div>
                    </>
                )}
                {!userContext?.userData?.isVendor && (
                    <Button variant="outlined" onClick={handleBuyTickets}>
                        Buy Tickets
                    </Button>
                )}
            </div>
        </div>
    );
};

export default Event;
