using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using System.Net;

namespace ObligatorioISP.WebAPI
{
    public class ErrorActionResultFactory
    {
        ControllerBase sender;
        public ErrorActionResultFactory(ControllerBase controller)
        {
            sender = controller;
        }

        public IActionResult GenerateError(ServiceException e)
        {
            IActionResult errorResponse = null;
            ErrorDto errorMessage = new ErrorDto() { ErrorMessage = e.Message };
            switch (e.Error)
            {
                case ErrorType.ENTITY_NOT_FOUND:
                    errorResponse = sender.NotFound(errorMessage);
                    break;
                case ErrorType.DATA_CORRUPTED:
                case ErrorType.DATA_INACCESSIBLE:
                    errorResponse = sender.StatusCode((int)HttpStatusCode.InternalServerError, errorMessage);
                    break;
            }
            return errorResponse;
        }

        private IActionResult GenerateBadRequest(ServiceException e)
        {
            ErrorDto errorMessage = new ErrorDto() { ErrorMessage = e.Message };
            return sender.BadRequest(errorMessage);
        }
    }
}
