// StartPage.tsx
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Register from "./Register";
import Login from "../Common/Login";
import Dashboard from "../Common/Dashboard";
import { UserProvider } from "./UserContext";
import Event from "./Event";
import Simulation from "../Simulation/Simulation";
import ChangeTickets from "./ChangeTickets";
import AddVendors from "../Vendor/AddVendors";
import CreateEvent from "../Vendor/CreateEvent";
import UpdateEvent from "../Vendor/UpdateEvent";

function StartPage() {
    return (
        <UserProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/event/:eventId" element={<Event />} />
                    <Route path="/simulation/:eventId" element={<Simulation />} />
                    <Route path="/changeTickets/:eventId" element={<ChangeTickets />} />
                    <Route path="/addVendors/:eventId" element={<AddVendors />} />
                    <Route path="/createEvent" element={<CreateEvent />} />
                    <Route path="/updateEvent/:eventId" element={<UpdateEvent />} />
                </Routes>
            </Router>
        </UserProvider>
    );
}

export default StartPage;