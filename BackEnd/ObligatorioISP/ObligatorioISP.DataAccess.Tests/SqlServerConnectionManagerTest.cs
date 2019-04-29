using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    class SqlServerConnectionManagerTest
    {
        private SqlServerConnectionManager connection;
        [TestInitialize]
        public void SetUp() {
          string unexistentServer = "DESKTOP-JH1M2MF\\SQLSERVER_R15;Initial Catalog=DB;Trusted_Connection=True;Integrated Security=True;";
          connection = new SqlServerConnectionManager(unexistentServer);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantReadData() {
            string query = "query";
            connection.ExcecuteRead(query);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantExcecuteCommmand() {
            string command = "command";
            connection.ExcecuteCommand(command);
        }
    }
}
