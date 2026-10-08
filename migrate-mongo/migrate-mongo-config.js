// In this file you can configure migrate-mongo

const mongoUri =
  process.env.MIGRATE_MONGO_URI ||
  process.env.SPRING_DOCKER_MONGODB_URI ||
  process.env.SPRING_MONGODB_URI ||
  "mongodb://localhost:27017";

let databaseName =
  process.env.MIGRATE_MONGO_DATABASE ||
  process.env.SPRING_MONGODB_DATABASE ||
  "example_db";

try {
  const parsed = new URL(
    mongoUri.replace(/^mongodb:\/\//, "http://").replace(/^mongodb\+srv:\/\//, "https://")
  );
  const pathname = parsed.pathname.replace(/^\//, "");
  if (pathname) {
    databaseName = pathname.split("?")[0] || databaseName;
  }
} catch {
  // Use fallback databaseName
}

const config = {
  mongodb: {
    url: mongoUri,
    databaseName: databaseName,
    options: {}
  },

  // The migrations dir, can be an relative or absolute path. Only edit this when really necessary.
  migrationsDir: "migrations",

  // The mongodb collection where the applied changes are stored. Only edit this when really necessary.
  changelogCollectionName: "changelog",

  // The mongodb collection where the lock will be created.
  lockCollectionName: "changelog_lock",

  // The value in seconds for the TTL index that will be used for the lock. Value of 0 will disable the feature.
  lockTtl: 0,

  // The file extension to create migrations and search for in migration dir 
  migrationFileExtension: ".js",

  // Enable the algorithm to create a checksum of the file contents and use that in the comparison to determine
  // if the file should be run.  Requires that scripts are coded to be run multiple times.
  useFileHash: false,

  // Don't change this, unless you know what you're doing
  moduleSystem: 'commonjs',
};

module.exports = config;
