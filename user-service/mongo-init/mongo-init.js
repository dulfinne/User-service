try {
    rs.status();
} catch (err) {
    rs.initiate({
        _id: 'rs0',
        members: [
            { _id: 0, host: 'mongo1:27027' },
        ]
    });
}

db.getSiblingDB("admin").createUser({
    user: "debezium",
    pwd: "debezium",
    roles: [
        { role: "readAnyDatabase", db: "admin" },
        { role: "clusterMonitor", db: "admin" },
        { role: "read", db: "config" }
    ]
});
