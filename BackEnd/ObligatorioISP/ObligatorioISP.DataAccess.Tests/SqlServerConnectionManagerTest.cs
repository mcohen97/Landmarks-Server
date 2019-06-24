using Microsoft.Extensions.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts.Exceptions;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class SqlServerConnectionManagerTest
    {
        private SqlServerConnectionManager connection;
        [TestInitialize]
        public void SetUp()
        {
            IConfigurationRoot config = new ConfigurationBuilder()
                                        .AddJsonFile("testconfig.json")
                                        .Build();
            string serverString = config["serverString"];
            string securityString = config["securityString"];
            string unexistentServer = $"{serverString}Initial Catalog=unexistentDB;{securityString}";
            connection = new SqlServerConnectionManager(unexistentServer);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantReadData()
        {
            string query = "query";
            connection.ExcecuteRead(query);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantExcecuteCommmand()
        {
            string command = "command";
            connection.ExcecuteCommand(command);
        }
    }
}
