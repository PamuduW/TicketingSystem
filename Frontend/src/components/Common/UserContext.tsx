import React, { createContext, useState, ReactNode } from "react";

interface UserData {
    username: string;
    userId: string;
    isVendor: boolean;
}

interface UserContextType {
    userData: UserData | null;
    setUserData: (data: UserData) => void;
}

/**
 * Context to store and manage user data.
 */
export const UserContext = createContext<UserContextType | undefined>(
    undefined
);

/**
 * UserProvider component to provide user context to its children.
 * @param children - The child components that will consume the user context.
 */
export const UserProvider: React.FC<{ children: ReactNode }> = ({
    children,
}) => {
    // State to store user data
    const [userData, setUserData] = useState<UserData | null>(null);

    return (
        <UserContext.Provider value={{ userData, setUserData }}>
            {children}
        </UserContext.Provider>
    );
};