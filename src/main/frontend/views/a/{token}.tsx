import {useParams} from "react-router-dom";
import {Button} from "@vaadin/react-components";
import {useEffect, useState} from "react";
import SurveyQuestionDTO from "Frontend/generated/app/vaadin/nps/service/SurveyQuestionDTO";
import {ResponseService} from "Frontend/generated/endpoints";


export default function SurveyAnswer() {
  const {token} = useParams();
  const [surveyQuestion, setSurveyQuestion] = useState<SurveyQuestionDTO>();

  useEffect(() => {
    if (token) {
      ResponseService.getSurveyQuestion(token).then(setSurveyQuestion);
    }
  }, [token]);

  async function handleButtonClick(value: number) {
    await ResponseService.saveResponse({
      token: token!,
      score: value
    });
    setSurveyQuestion(await ResponseService.getSurveyQuestion(token!));
  };

  return (
    <div className="p-m flex flex-col gap-s">

      {surveyQuestion && surveyQuestion.alreadyAnswered && (
        <p>Thank you for your feedback!</p>
      )}

      {surveyQuestion && !surveyQuestion.alreadyAnswered && (
        <>
          <p>{surveyQuestion.question}</p>
          <div className="flex gap-s">
            {[...Array(11).keys()].map((value) => (
              <Button key={value} onClick={() => handleButtonClick(value)}>
                {value}
              </Button>
            ))}
          </div>
        </>
      )}
    </div>
  );
}

function questionForm() {
  throw new Error("Function not implemented.");
}